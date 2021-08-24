package com.example.musicapplocal.ui.main

import MainContract
import android.animation.ObjectAnimator
import android.content.ComponentName
import android.content.Intent
import android.content.IntentFilter
import android.content.ServiceConnection
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.IBinder
import android.view.View
import android.view.animation.Animation
import android.view.animation.LinearInterpolator
import android.widget.SeekBar
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.musicapplocal.R
import com.example.musicapplocal.databinding.ActivityMainBinding
import com.example.musicapplocal.broadcast.MusicBroadcast
import com.example.musicapplocal.data.model.Music
import com.example.musicapplocal.service.MusicPlayerService
import com.example.musicapplocal.ui.library.LibraryActivity
import com.example.musicapplocal.ui.notification.MusicNotificationCallback
import com.example.musicapplocal.utils.AppConstants
import com.example.musicapplocal.utils.Common
import com.example.musicapplocal.utils.RepositoryUtils

class MainActivity : AppCompatActivity(),
    View.OnClickListener,
    MainContract.View, SeekBar.OnSeekBarChangeListener,
    MusicNotificationCallback {

    private val permissions = arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE)
    private lateinit var binding: ActivityMainBinding
    private var controlButtons = listOf<TextView>()
    private lateinit var runnable: Runnable
    private val handler = Handler()
    private lateinit var musicPlayerService: MusicPlayerService
    private var musicBroadcast: MusicBroadcast? = null
    private val serviceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            val binder = service as MusicPlayerService.MusicBinder
            musicPlayerService = binder.getService()
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            musicPlayerService.stopSelf()
        }

    }
    private var anim = ObjectAnimator()
    private val musics = mutableListOf<Music>()
    private lateinit var nameSong: String
    private lateinit var author: String
    private lateinit var id: String
    internal lateinit var presenter: MainContract.Presenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initViewBinding()
        initComponents()
    }

    private fun initComponents() {
        if (checkPermission()) {
            initView()
            initService()
            initData()
            setAnimationDisk()
            setOnClicks()
        } else requestPermission()
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            requestPermissions(permissions, 0)
    }

    private fun initView() {
        binding.run {
            seekMainTime.setOnSeekBarChangeListener(this@MainActivity)
            listOf(
                textMainNext,
                textMainPlay,
                textMainPrev,
                textMainReplay
            ).forEach {
                it.setOnClickListener(this@MainActivity)
            }
        }
    }

    private fun checkPermission(): Boolean {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
            for (p in permissions) {
                if (checkSelfPermission(p) == PackageManager.PERMISSION_DENIED)
                    return false
            }
        return true
    }


    private fun initData() {
        val repository = RepositoryUtils.getMusicRepository(contentResolver)
        setPresenter(MainPresenter(this, repository))
        getDataSongFromIntent()
    }

    private fun initService() {
        musicPlayerService = MusicPlayerService()
        bindService(
            MusicPlayerService.getIntent(this),
            serviceConnection,
            BIND_AUTO_CREATE
        )
        musicBroadcast = MusicBroadcast(this)
        IntentFilter().apply {
            addAction(AppConstants.NEXT)
            addAction(AppConstants.PLAY_PAUSE)
            addAction(AppConstants.PREV)
        }.run {
            registerReceiver(musicBroadcast, this)
        }
    }

    private fun getDataSongFromIntent() {
        val music = intent?.extras?.getSerializable(AppConstants.EXTRA_MUSIC) as Music
        setTextMusic(music)
        presenter.loadDataSong()
    }

    private fun setTextMusic(music: Music) {
        nameSong = music.name.toString()
        author = music.author.toString()
        id = music.id.toString()
        binding.textMainName.text = nameSong
        binding.textMainAuthor.text = author
    }

    private fun setAnimationDisk() {
        anim = ObjectAnimator.ofFloat(
            binding.imageMainRound,
            View.ROTATION,
            AppConstants.ROTATE_START,
            AppConstants.ROTATE_END
        )
            .setDuration(AppConstants.SPEED_DISK.toLong())
        anim.repeatCount = Animation.INFINITE
        anim.interpolator = LinearInterpolator()
        anim.start()
    }

    override fun loadListMusic(listMusics: ArrayList<Music>) {
        this.musics.addAll(listMusics)
    }

    override fun loadMusicFailed() {
        Toast.makeText(this, R.string.notify_music_failed, Toast.LENGTH_SHORT).show()
    }

    override fun loadNextMusic(music: Music) {
        setTextMusic(music)
        musicPlayerService.createMedia(this@MainActivity, music)
        musicPlayerService.play()
        binding.textMainPlay.setBackgroundResource(R.drawable.ic_pause)
        updateSeekBar()
    }

    override fun loadPrevMusic(music: Music) {
        setTextMusic(music)
        musicPlayerService.createMedia(this@MainActivity, music)
        musicPlayerService.play()
        binding.textMainPlay.setBackgroundResource(R.drawable.ic_pause)
        updateSeekBar()
    }

    override fun setPresenter(presenter: MainContract.Presenter) {
        this.presenter = presenter
    }

    private fun setOnClicks() {
        with(binding) {
            controlButtons = listOf(
                textMainBack,
                textMainMore,
                textMainReplay,
                textMainPrev,
                textMainPlay,
                textMainNext,
                textMainMenu
            )
        }
        controlButtons.forEach { it.setOnClickListener(this) }
    }

    private fun initViewBinding() {
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    @RequiresApi(Build.VERSION_CODES.KITKAT)
    override fun onClick(view: View?) = with(binding) {
        if (view is TextView) {
            when (view) {
                textMainPlay -> playOrPauseMusic()
                textMainNext -> nextMusic()
                textMainPrev -> prevMusic()
                textMainReplay -> replayMusic()
                else -> Unit
            }
        }
    }

    private fun replayMusic() {
        musicPlayerService.createMedia(this@MainActivity, Music(id, nameSong, author))
        changeStatusPlay(true)
    }

    private fun prevMusic() {
        presenter.loadDataPrevMusic(id, this.musics)
    }

    private fun nextMusic() {
        presenter.loadDataNextMusic(id, this.musics)
    }

    private fun playOrPauseMusic() {
        if (musicPlayerService.isPlaying() == true) {
            changeStatusPlay(false)
        } else {
            musicPlayerService.createMedia(this@MainActivity, Music(id, nameSong, author))
            changeStatusPlay(true)
        }
        updateSeekBar()
    }

    override fun onBackPressed() {
        musicPlayerService.stop()
        startActivity(Intent(this@MainActivity, LibraryActivity::class.java))
        super.onBackPressed()
    }

    override fun onDestroy() {
        super.onDestroy()
        musicPlayerService.stopSelf()
        unregisterReceiver(musicBroadcast)
    }

    override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
    }

    override fun onStartTrackingTouch(seekBar: SeekBar?) {
        seekBar?.progress?.let {
            changeStatusPlay(true)
            musicPlayerService.seekTo(it)
        }
    }

    override fun onStopTrackingTouch(seekBar: SeekBar?) {
        seekBar?.progress?.let {
//            changeStatusPlay(true)
            musicPlayerService.seekTo(it)
        }
    }

    private fun changeStatusPlay(change: Boolean) {
        if (change) {
            musicPlayerService.play()
            binding.textMainPlay.setBackgroundResource(R.drawable.ic_pause)
        } else {
            musicPlayerService.pause()
            binding.textMainPlay.setBackgroundResource(R.drawable.ic_play)
        }
    }

    private fun updateSeekBar() {
        with(binding) {
            textMainTimeEnd.text = Common.getTimeFormat(musicPlayerService.getDuration())
            with(seekMainTime) {
                progress = AppConstants.PROGRESS_INIT
                max = musicPlayerService.getDuration()
                setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
                    override fun onProgressChanged(
                        view: SeekBar?,
                        progress: Int,
                        changed: Boolean
                    ) {
                        if (changed) {
                            musicPlayerService.seekTo(progress)
                        }
                    }

                    override fun onStartTrackingTouch(view: SeekBar?) {
                    }

                    override fun onStopTrackingTouch(view: SeekBar?) {
                    }

                })
            }
        }
        runnable = Runnable {
            musicPlayerService.getCurrentPosition()?.let {
                binding.seekMainTime.progress = it
                binding.textMainTimeStart.text = Common.getTimeFormat(it)
                if (musicPlayerService.isPlaying() == true) {
                    handler.postDelayed(runnable, AppConstants.TIME_DELAYED.toLong())
                }
            }
        }
        handler.postDelayed(runnable, AppConstants.TIME_DELAYED.toLong())
        musicPlayerService.mediaPlayer?.setOnCompletionListener {
            binding.seekMainTime.progress = AppConstants.PROGRESS_INIT
        }
    }

    override fun onNotifyPlayPause() {
        if (musicPlayerService.isPlaying() == true) {
            changeStatusPlay(false)
        } else {
            musicPlayerService.createMedia(this@MainActivity, Music(id, nameSong, author))
            changeStatusPlay(true)
        }
        updateSeekBar()
    }

    override fun onNotifyNext() {
        presenter.loadDataNextMusic(id, this.musics)
    }

    override fun onNotifyPrev() {
        presenter.loadDataPrevMusic(id, this.musics)
    }
}
