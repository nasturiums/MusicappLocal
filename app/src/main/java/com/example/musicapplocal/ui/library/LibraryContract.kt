package com.example.musicapplocal.ui.library

import android.app.Activity
import com.example.musicapplocal.data.model.Music
import com.example.musicapplocal.ui.base.BasePresenter
import com.example.musicapplocal.ui.base.BaseView

interface LibraryContract {
    interface Presenter : BasePresenter {
        fun checkPermission(activity: Activity)
        fun onPermissionsResult(
            requestCode: Int,
            permissions: Array<out String>,
            grantResults: IntArray,
            activity: Activity
        )
    }

    interface View : BaseView<Presenter> {
        fun displayListMusic(listMusics: ArrayList<Music>)
        fun loadMusicFailed()
    }
}
