package com.example.musicapplocal.data.repository

import com.example.musicapplocal.data.local.utils.OnDataLocalCallback
import com.example.musicapplocal.data.model.Music

interface MusicDataSource {
    fun getAllMusic(callback: OnDataLocalCallback<List<Music>>)
}
