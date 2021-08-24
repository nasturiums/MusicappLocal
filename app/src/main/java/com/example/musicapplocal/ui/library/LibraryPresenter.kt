package com.example.musicapplocal.ui.library

import android.app.Activity
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import com.example.musicapplocal.data.local.utils.OnDataLocalCallback
import com.example.musicapplocal.data.model.Music
import com.example.musicapplocal.data.repository.MusicRepository
import com.example.musicapplocal.utils.AppConstants

class LibraryPresenter(
    private var view: LibraryContract.View?,
    private var musicRepository: MusicRepository
) : LibraryContract.Presenter {

    override fun checkPermission(activity: Activity) {
        if (ActivityCompat.checkSelfPermission(
                activity,
                android.Manifest.permission.READ_EXTERNAL_STORAGE
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                activity,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                AppConstants.REQUEST_CODE
            )
        } else {
            loadMusic(activity)
        }
    }

    override fun onPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
        activity: Activity
    ) {
        if (requestCode == AppConstants.REQUEST_CODE && grantResults.first() == PackageManager.PERMISSION_GRANTED) {
            loadMusic(activity)
        }
    }

    private fun loadMusic(activity: Activity) {
        musicRepository.getAllMusic(object : OnDataLocalCallback<List<Music>> {
            override fun onSucceed(data: List<Music>) {
                view?.displayListMusic(data as ArrayList<Music>)
            }

            override fun onFailed(e: Exception?) {
                view?.loadMusicFailed()
            }

        })
    }

    override fun onDestroy() {
        this.view = null
    }

}
