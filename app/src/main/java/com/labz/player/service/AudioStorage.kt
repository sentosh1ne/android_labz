package com.labz.player.service

import android.content.Context
import android.database.Cursor
import android.provider.MediaStore
import com.labz.player.models.Audio
import com.labz.preferences.PreferencesHelper
import android.content.ContentUris
import android.net.Uri


/**
 * Created by Stanislav Vylegzhanin on 25.10.17.
 */
interface AudioStorage {
    fun saveAudio(audios: ArrayList<Audio>?)
    fun loadAudio(): ArrayList<Audio>
    fun saveAudioIndex(index: Int)
    fun loadAudioIndex(): Int
    fun clearCache()
}

class AudioStorageImpl(val context: Context) : AudioStorage {
    private val preferences = PreferencesHelper.getInstance(context)

    override fun saveAudio(audios: ArrayList<Audio>?) {
        preferences.saveCachedAudios(audios)
    }

    override fun loadAudio(): ArrayList<Audio> {
        return preferences.cachedAudios
    }

    override fun saveAudioIndex(index: Int) {
        preferences.savePlayingIndex(index)
    }

    override fun loadAudioIndex(): Int {
        return preferences.playingIndex
    }

    override fun clearCache() {
        preferences.saveCachedAudios(ArrayList())
    }
}

//todo add columns for album art
fun Cursor?.toAudioList(): ArrayList<Audio> {
    val audioList = ArrayList<Audio>()
    if (this != null && count > 0) {
        while (this.moveToNext()) {
            val data = getString(getColumnIndex(MediaStore.Audio.Media.DATA))
            val title = getString(getColumnIndex(MediaStore.Audio.Media.TITLE))
            val album = getString(getColumnIndex(MediaStore.Audio.Media.ALBUM))
            val artist = getString(getColumnIndex(MediaStore.Audio.Media.ARTIST))
            val albumId = getLong(getColumnIndex(MediaStore.Audio.Media.ALBUM_ID))
            val duration = getLong(getColumnIndex(MediaStore.Audio.Media.DURATION))

            val artworkUri = Uri.parse("content://media/external/audio/albumart")
            val albumArtUri = ContentUris.withAppendedId(artworkUri, albumId)

            audioList.add(Audio(data, title, album, artist, albumArtUri.toString(), duration))
        }
    }

    this?.close()
    return audioList
}

