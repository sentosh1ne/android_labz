package com.labz.player.ui

import android.annotation.SuppressLint
import android.content.*
import android.os.Bundle
import android.os.IBinder
import android.provider.MediaStore
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.labz.R
import com.labz.base.fragment.BaseFragment
import com.labz.player.adapter.AudioPlayerAdapter
import com.labz.player.models.Audio
import com.labz.player.service.AudioStorageImpl
import com.labz.player.service.MediaPlayerService
import com.labz.player.service.MediaPlayerService.PLAY_NEW_AUDIO
import com.labz.player.service.toAudioList
import com.labz.preferences.PreferencesHelper
import com.sothree.slidinguppanel.SlidingUpPanelLayout
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.fragment_music_player.*
import kotlinx.android.synthetic.main.layout_content_audio.*
import kotlinx.android.synthetic.main.layout_dropdown_player.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.sdk25.coroutines.onClick
import org.jetbrains.anko.sdk25.coroutines.onTouch
import java.util.*


/**
 * Created by Stanislav Vylegzhanin on 24.10.17.
 */
class PlayerFragment : BaseFragment(), AnkoLogger {

    private var player: MediaPlayerService? = null
    private var serviceBound = false
    private var audioList: ArrayList<Audio>? = null
    private lateinit var adapter: AudioPlayerAdapter
    private lateinit var preferences: PreferencesHelper
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            // We've bound to LocalService, cast the IBinder and get LocalService instance
            val binder = service as MediaPlayerService.LocalBinder
            player = binder.service
            serviceBound = true
        }

        override fun onServiceDisconnected(name: ComponentName) {
            serviceBound = false
        }
    }

    private var seekTimer: Timer? = null

    private val uiUpdateReceiver = object : BroadcastReceiver() {
        override fun onReceive(p0: Context?, intent: Intent?) {
            if (intent?.extras != null && intent.extras.get(PLAY_NEW_AUDIO) != null) {
                preferences.savePlayingIndex(0)
                playAudio(0)
            }
            initDropdownSong(adapter.getItem(preferences.playingIndex))
        }
    }

    companion object {
        val UI_UPDATE_BROADCAST = "ui_update_broadcast"
        val DURATION_EXTRA = "duration"
        val ACTION_EXTRA = "action"

        fun newInstance(): Fragment {
            val args = Bundle()
            val fragment = PlayerFragment()
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(inflater: LayoutInflater?, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater?.inflate(R.layout.fragment_music_player, container, false)
    }

    override fun onStart() {
        super.onStart()
        activity.registerReceiver(uiUpdateReceiver, IntentFilter(UI_UPDATE_BROADCAST))
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        preferences = PreferencesHelper.getInstance(context)
        initList()
        loadAudio()

        seekDuration.onTouch { v, _ ->
            val msecs = seekDuration.progress * 1000
            val intent = Intent(MediaPlayerService.PLAYER_ACTION_BROADCAST)
            intent.putExtra(DURATION_EXTRA, msecs)
            intent.putExtra(ACTION_EXTRA, MediaAction.SEEK)
            activity.sendBroadcast(intent)
        }

        btnPreviousSong.onClick {
            sendActionBroadcast(MediaAction.PREVIOUS)
        }

        btnNextSong.onClick {
            sendActionBroadcast(MediaAction.NEXT)
        }

        btnPauseSong.onClick {
            //            btnPauseSong.isChecked = !btnPauseSong.isChecked
            sendActionBroadcast(MediaAction.PAUSE)
        }

        btnShuffle.onClick {
            moveCurrentSongToStart()
            Collections.shuffle(adapter.items)
            adapter.notifyDataSetChanged()
            preferences.saveCachedAudios(ArrayList(adapter.items))
            sendActionBroadcast(MediaAction.SHUFFLE)
        }
    }

    private fun moveCurrentSongToStart() {
        val currentAudio = adapter.items.get(preferences.playingIndex)
        adapter.items.add(0, currentAudio)
    }

    private fun sendActionBroadcast(action: MediaAction) {
        val intent = Intent(MediaPlayerService.PLAYER_ACTION_BROADCAST)
        intent.putExtra(ACTION_EXTRA, action)
        activity.sendBroadcast(intent)
    }

    private fun initList() {
        rvAudios.layoutManager = LinearLayoutManager(context)
        adapter = AudioPlayerAdapter(context, audioList)
        adapter.setOnViewClickListener({ _: View, position: Int, _: Long ->
            slidingLayout.panelState = SlidingUpPanelLayout.PanelState.EXPANDED
            slidingLayout.isTouchEnabled = false
            playAudio(position)
            val chosenAudio = adapter.getItem(position)
            initDropdownSong(chosenAudio)
        })
        rvAudios.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    private fun initDropdownSong(audio: Audio?) {
        seekTimer?.let {
            seekTimer?.cancel()
            seekDuration.progress = 0
        }

        Picasso.with(context)
                .load(audio?.albumPath)
                .error(R.drawable.ic_launcher_background)
                .fit()
                .into(imgSongCover)
        txtCurrentDuration.text = 0.toString()
        txtDurationMax.text = audio?.formattedDuration()
        txtTitle.text = audio?.title

//        seekTimer = timer(initialDelay = 1000, period = 1000, action = {
//            onUiThread {
//                updateSeekBar()
//            }
//        })
    }

    private fun updateSeekBar() {
        seekDuration.progress += 1
        val mins = seekDuration.progress / 60
        val seconds = seekDuration.progress % 60
        var secondsString: String

        secondsString = if (seconds <= 9) {
            "0" + seconds
        } else {
            seconds.toString()
        }
        txtCurrentDuration.text = "0$mins:$secondsString"
    }

    private fun playAudio(audioIndex: Int) {
        val storage = AudioStorageImpl(activity.applicationContext)
        storage.saveAudioIndex(audioIndex)

        if (!serviceBound) {
            storage.saveAudio(audioList)
            val playerIntent = Intent(activity.applicationContext, MediaPlayerService::class.java)
            activity.applicationContext.startService(playerIntent)
            activity.bindService(playerIntent, serviceConnection, Context.BIND_AUTO_CREATE)
        } else {
            val broadcastIntent = Intent(MediaPlayerService.PLAY_NEW_AUDIO)
            activity.sendBroadcast(broadcastIntent)
        }
    }

    private fun loadAudio() {
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val selection = MediaStore.Audio.Media.IS_MUSIC + "!= 0"
        val sortOrder = MediaStore.Audio.Media.TITLE + " ASC"

        val cursor = activity.contentResolver.query(uri, null, selection, null, sortOrder)

        audioList = cursor.toAudioList()
        adapter.items = audioList
        val prefs = PreferencesHelper.getInstance(activity.applicationContext)
        prefs.saveCachedAudios(audioList)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (serviceBound) {
            activity.unbindService(serviceConnection)
        }
        player?.stopSelf()
        activity.unregisterReceiver(uiUpdateReceiver)
    }

    override fun onDetach() {
        seekTimer?.cancel()
        super.onDetach()
    }
}

enum class MediaAction {
    PAUSE, NEXT, PREVIOUS, SHUFFLE, SEEK
}
