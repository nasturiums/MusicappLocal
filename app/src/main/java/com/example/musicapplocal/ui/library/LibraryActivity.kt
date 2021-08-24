package com.example.musicapplocal.ui.library

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapplocal.R
import com.example.musicapplocal.databinding.ActivityLibraryBinding
import com.example.musicapplocal.data.model.Music
import com.example.musicapplocal.ui.main.MainActivity
import com.example.musicapplocal.utils.AppConstants
import com.example.musicapplocal.utils.RepositoryUtils

class LibraryActivity : AppCompatActivity(), LibraryContract.View {
    private lateinit var binding: ActivityLibraryBinding
    private val adapter = MusicAdapter(this::onMusicClick)
    internal lateinit var presenter: LibraryContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_library)
        initViewBinding()
        initData()
        initAdapter()
        presenter.checkPermission(this)
    }

    private fun initAdapter() {
        binding.recyclerLibrary.adapter = adapter
    }

    private fun initData() {
        val repository = RepositoryUtils.getMusicRepository(contentResolver)
        setPresenter(LibraryPresenter(this, repository))
    }

    private fun initViewBinding() {
        binding = ActivityLibraryBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun setPresenter(presenter: LibraryContract.Presenter) {
        this.presenter = presenter
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        presenter.onPermissionsResult(requestCode, permissions, grantResults, this)
    }

    override fun displayListMusic(listMusics: ArrayList<Music>) {
        adapter.updateListMusic(listMusics)
    }

    override fun loadMusicFailed() {
        Toast.makeText(this, resources.getString(R.string.notify_music_failed), Toast.LENGTH_SHORT)
            .show()
    }

    private fun onMusicClick(music: Music) {
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra(AppConstants.EXTRA_MUSIC, music)
        startActivity(intent)
    }

}
