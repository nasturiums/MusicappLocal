package com.example.musicapplocal.data.repository

import com.example.musicapplocal.data.local.utils.OnDataLocalCallback
import com.example.musicapplocal.data.model.Music

class MusicRepository private constructor(
    private val local: MusicDataSource
) : MusicDataSource {

    override fun getAllMusic(callback: OnDataLocalCallback<List<Music>>) {
        local.getAllMusic(callback)
    }

    companion object {
        private var instance: MusicRepository? = null
        fun getInstance(local: MusicDataSource) =
            instance ?: MusicRepository(local).also { instance = it }
    }

}
