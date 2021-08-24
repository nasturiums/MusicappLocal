package com.example.musicapplocal.broadcast

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.example.musicapplocal.ui.notification.MusicNotificationCallback
import com.example.musicapplocal.utils.AppConstants.NEXT
import com.example.musicapplocal.utils.AppConstants.PLAY_PAUSE
import com.example.musicapplocal.utils.AppConstants.PREV

class MusicBroadcast(private val musicCallback: MusicNotificationCallback?) : BroadcastReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        when (intent?.action) {
            NEXT -> musicCallback?.onNotifyNext()
            PLAY_PAUSE -> musicCallback?.onNotifyPlayPause()
            PREV -> musicCallback?.onNotifyPrev()
        }
    }
}
