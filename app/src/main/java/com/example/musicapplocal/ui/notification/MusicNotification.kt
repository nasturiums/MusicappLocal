package com.example.musicapplocal.ui.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.musicapplocal.R
import com.example.musicapplocal.service.MusicPlayerService
import com.example.musicapplocal.utils.AppConstants
import com.example.musicapplocal.utils.AppConstants.BROADCAST_ID
import com.example.musicapplocal.utils.AppConstants.CHANNEL_ID
import com.example.musicapplocal.utils.AppConstants.NEXT
import com.example.musicapplocal.utils.AppConstants.NOTIFICATION_MUSIC_ID
import com.example.musicapplocal.utils.AppConstants.PLAY_PAUSE
import com.example.musicapplocal.utils.AppConstants.PREV

class MusicNotification(private val service: MusicPlayerService) {
    private var notificationManager: NotificationManager? = null
    private var remoteView: RemoteViews? = null

    fun createNotification() {
        createChannel()
        val notification = NotificationCompat
            .Builder(service, AppConstants.CHANNEL_ID)
            .setSmallIcon(R.drawable.ic_headphone_24)
            .setCustomBigContentView(createRemoteView())
            .setSound(null)
            .setDefaults(0)
            .build()
        notificationManager?.notify(NOTIFICATION_MUSIC_ID, notification)

    }

    private fun createRemoteView(): RemoteViews? {
        remoteView = RemoteViews(service.packageName, R.layout.remote_play_music)
        //remoteView?.setTextViewText(R.id.text_remote_music_name,service.getNameMusic())
        createPendingIntent(PLAY_PAUSE, R.id.text_remote_play)
        createPendingIntent(NEXT, R.id.text_remote_next)
        createPendingIntent(PREV, R.id.text_remote_prev)
        return remoteView

    }

    private fun createPendingIntent(action: String, viewId: Int) {
        val pendingIntent =
            PendingIntent.getBroadcast(service, BROADCAST_ID, Intent(action), 0)
        remoteView?.setOnClickPendingIntent(viewId, pendingIntent)
    }

    private fun createChannel() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_ID,
                NotificationManager.IMPORTANCE_LOW
            )
            notificationManager = service.getSystemService(NotificationManager::class.java)
            notificationManager?.createNotificationChannel(channel)
        }

    }
}
