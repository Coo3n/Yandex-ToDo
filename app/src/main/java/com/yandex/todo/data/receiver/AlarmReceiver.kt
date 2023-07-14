package com.yandex.todo.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.yandex.todo.MyApp
import com.yandex.todo.data.remote.notification.NotificationManager
import com.yandex.todo.data.remote.notification.TodoAlarmManager
import com.yandex.todo.domain.model.TodoItem
import javax.inject.Inject
import dagger.Lazy


class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationManager: Lazy<NotificationManager>

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("NOTIFICATION", "onReceive")
        (context?.applicationContext as MyApp).appComponent
            .createTodoComponentFactory()
            .create()
            .injectAlarmReceiver(this)
        Log.i("NOTIFICATION", "injectAlarmReceiver")

        val taskDescription = intent?.getStringExtra(TodoAlarmManager.TASK_DESCRIPTION) ?: "exist"
        val importanceLevel = intent?.getStringExtra(TodoAlarmManager.IMPORTANCE_LEVEL) ?: "exist"

        if (taskDescription == "exist" && importanceLevel == "exist") {
            Log.i("NOTIFICATION", "todoItem AlarmReceiver")
            notificationManager.get().createNotification(taskDescription, importanceLevel)
        }
    }
}