package com.example.musicapplocal.data.local

import android.content.ContentResolver
import android.database.Cursor
import android.provider.MediaStore
import com.example.musicapplocal.data.local.preferences.utils.LocalAsyncTask
import com.example.musicapplocal.data.local.utils.OnDataLocalCallback
import com.example.musicapplocal.data.model.Music
import com.example.musicapplocal.data.repository.MusicDataSource

class MusicLocalData private constructor(private val contentResolver: ContentResolver) : MusicDataSource {

    override fun getAllMusic(callback: OnDataLocalCallback<List<Music>>) {
        LocalAsyncTask<Unit, List<Music>>(callback) {
            getAllAudioFromDevice()
        }.execute(Unit)
    }

    fun getAllAudioFromDevice(): List<Music> {
        val tempAudioList = ArrayList<Music>()
        val uri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI
        val projection = arrayOf<String>(
            MediaStore.Audio.AudioColumns._ID,
            MediaStore.Audio.AudioColumns.TITLE,
            MediaStore.Audio.ArtistColumns.ARTIST
        )
        val cursor: Cursor? = contentResolver.query(uri, projection, null, null, null)
        cursor?.let {
            while (it.moveToNext()) {
                val id = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID))
                val title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE))
                val artist = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST))
                tempAudioList.add(Music(id, title, artist))
            }
        }
        cursor?.close()
        return tempAudioList
    }

    companion object {
        private var instance: MusicLocalData? = null
        fun getInstance(contentResolver: ContentResolver) =
            instance ?: MusicLocalData(contentResolver).also { instance = it }
    }
}
