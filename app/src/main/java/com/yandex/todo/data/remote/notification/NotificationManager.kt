package com.yandex.todo.data.remote.notification

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.util.Log
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.yandex.todo.R
import com.yandex.todo.di.scope.AppScope
import javax.inject.Inject

@AppScope
class NotificationManager @Inject constructor(
    private var applicationContext: Context
) {
    init {
        createNotificationChannel()
    }

    fun createNotification(
        taskDescription: String,
        importanceLevel: String
    ) { // Создание и проверка Permission
        var contentNotification = taskDescription
        if (contentNotification.isEmpty()) {
            contentNotification = "Empty task"
        }

        val notification = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon(R.drawable.icon_add)
            .setContentTitle(contentNotification)
            .setContentText("Important: $importanceLevel")
            .setPriority(NotificationCompat.PRIORITY_MAX)
            .setAutoCancel(true)

        val notificationManager = NotificationManagerCompat.from(applicationContext)
        if (ActivityCompat.checkSelfPermission(
                applicationContext,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        Log.i("NOTIFICATION", "createNotification")
        notificationManager.notify(NOTIFICATION_ID, notification.build())
    }

    private fun createNotificationChannel() { // Создаем канал для уведомлений
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_DEFAULT
            )

            val notificationManager = applicationContext.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            Log.i("NOTIFICATION", "createNotificationChannel")
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        const val CHANNEL_NAME = "CHANNEL"
        const val CHANNEL_ID = "1"
        const val NOTIFICATION_ID = 1
    }
}