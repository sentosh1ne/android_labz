package com.labz.player.models

import android.net.Uri
import java.io.Serializable

/**
 * Created by Stanislav Vylegzhanin on 24.10.17.
 */
data class Audio(var data: String?, var title: String?,
                 var album: String?, var artist: String?,
                 var albumPath: String?, val duration: Long?) : Serializable {

    fun formattedDuration(): String {
        val minutes = this.duration!! / 60000 % 60000
        val seconds = this.duration % 60000 / 1000
        val secondsString = if (seconds <= 9) {
            "0" + seconds
        } else {
            seconds.toString()
        }
        return "0$minutes:$secondsString"
    }
}

enum class PlaybackStatus {
    PLAYING,
    PAUSED
}

