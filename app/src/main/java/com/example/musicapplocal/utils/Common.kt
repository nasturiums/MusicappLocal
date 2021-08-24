package com.example.musicapplocal.utils

import android.content.Context
import android.provider.MediaStore
import com.example.musicapplocal.data.model.Music
import java.util.concurrent.TimeUnit

class Common {
    companion object {
        @JvmStatic
        fun getTimeFormat(millis: Int): String {
            return String.format(
                AppConstants.TIME_FORMAT,
                TimeUnit.MILLISECONDS.toMinutes(millis.toLong()) % TimeUnit.HOURS.toMinutes(1),
                TimeUnit.MILLISECONDS.toSeconds(millis.toLong()) % TimeUnit.MINUTES.toSeconds(1)
            )
        }

        fun getAllAudioFromDevice(context: Context): List<Music>? {
            val tempAudioList = ArrayList<Music>()
            val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
            val projection = arrayOf<String>(
                MediaStore.Audio.AudioColumns.DATA,
                MediaStore.Audio.AudioColumns.TITLE,
                MediaStore.Audio.ArtistColumns.ARTIST,
            )
            return tempAudioList
        }
    }
}
