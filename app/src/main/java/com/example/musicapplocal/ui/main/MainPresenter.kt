package com.example.musicapplocal.ui.main

import MainContract
import com.example.musicapplocal.data.local.utils.OnDataLocalCallback
import com.example.musicapplocal.data.model.Music
import com.example.musicapplocal.data.repository.MusicRepository

class MainPresenter(
    view: MainContract.View,
    private val musicRepository: MusicRepository
) : MainContract.Presenter {

    private var view: MainContract.View? = view

    override fun loadDataNextMusic(id: String, musics: MutableList<Music>) {
        var pos = POSITION_START
        for ((index, value) in musics.withIndex()) {
            if (musics[index].id == id && index < musics.size - 1) pos = index + 1
        }
        view?.loadNextMusic(musics[pos])
    }

    override fun loadDataPrevMusic(id: String, musics: MutableList<Music>) {
        var pos = POSITION_START
        for ((index, value) in musics.withIndex()) {
            if (musics[index].id == id && index > POSITION_START) pos = index - 1
        }
        view?.loadPrevMusic(musics[pos])
    }

    override fun loadDataSong() {
        musicRepository.getAllMusic(object : OnDataLocalCallback<List<Music>> {
            override fun onSucceed(data: List<Music>) {
                view?.loadListMusic(data as ArrayList<Music>)
            }

            override fun onFailed(e: Exception?) {
                view?.loadMusicFailed()
            }

        })
    }

    override fun onDestroy() {
        this.view = null
    }

    companion object {
        const val POSITION_START = 0
    }
}
