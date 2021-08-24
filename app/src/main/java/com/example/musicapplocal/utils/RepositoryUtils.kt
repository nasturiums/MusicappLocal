package com.example.musicapplocal.utils

import android.content.ContentResolver
import com.example.musicapplocal.data.local.MusicLocalData
import com.example.musicapplocal.data.repository.MusicRepository

object RepositoryUtils {

    fun getMusicRepository(contentResolver: ContentResolver): MusicRepository {
        val local = MusicLocalData.getInstance(contentResolver)
        return MusicRepository.getInstance(local)
    }
}
