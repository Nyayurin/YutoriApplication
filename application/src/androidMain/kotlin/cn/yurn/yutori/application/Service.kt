package cn.yurn.yutori.application

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Intent
import android.os.IBinder
import androidx.core.app.NotificationCompat

class SavingService : Service() {
    private lateinit var notificationManager: NotificationManager

    override fun onCreate() {
        super.onCreate()
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(
            NotificationChannel("foreground", "保活", NotificationManager.IMPORTANCE_HIGH)
        )
        notificationManager.createNotificationChannel(
            NotificationChannel("messaging", "消息通知", NotificationManager.IMPORTANCE_HIGH)
        )
        startForeground(1, NotificationCompat.Builder(this, "foreground").build())
    }

    override fun onBind(intent: Intent?): IBinder? = null
}