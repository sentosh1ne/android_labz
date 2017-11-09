package com.labz.player.adapter

import android.content.Context
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.labz.R
import com.labz.base.adapter.RecyclerViewAdapter
import com.labz.player.models.Audio
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.layout_playback_controls.view.*
import org.jetbrains.anko.AnkoLogger
import org.jetbrains.anko.sdk25.coroutines.onClick

/**
 * Created by Stanislav Vylegzhanin on 25.10.17.
 */
class AudioPlayerAdapter(context: Context?, objects: MutableList<Audio>?) :
        RecyclerViewAdapter<Audio, AudioPlayerAdapter.ViewHolder>(context, objects), AnkoLogger {

    override fun onCreateViewHolder(parent: ViewGroup?, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent?.context).inflate(R.layout.layout_playback_controls, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder?, position: Int) {
        holder?.bind(getItem(position))
        holder?.itemView?.containerSong?.onClick {
            mOnViewClickListener.onViewClick(holder.itemView, position, 0L)
            notifyItemChanged(position)
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        fun bind(audio: Audio?) {
            itemView.txtSongTitle.text = audio?.title
            itemView.txtArtist.text = audio?.artist
            Picasso.with(itemView.context)
                    .load(audio?.albumPath)
                    .error(R.drawable.ic_launcher_background)
                    .fit()
                    .into(itemView.imgAlbumArt)
        }
    }
}
