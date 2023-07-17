package com.yandex.todo.data.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.util.Log
import com.yandex.todo.MyApp
import com.yandex.todo.data.remote.notification.NotificationManager
import com.yandex.todo.data.remote.notification.TodoAlarmManager
import dagger.Lazy
import javax.inject.Inject


class AlarmReceiver : BroadcastReceiver() {
    @Inject
    lateinit var notificationManager: Lazy<NotificationManager>

    override fun onReceive(context: Context?, intent: Intent?) {
        Log.i("NOTIFICATIONNN", "onReceive")
        (context?.applicationContext as MyApp).appComponent
            .createTodoComponentFactory()
            .create()
            .injectAlarmReceiver(this)
        Log.i("NOTIFICATIONNN", "injectAlarmReceiver")

        val taskDescription = intent?.getStringExtra(TodoAlarmManager.TASK_DESCRIPTION) ?: "exist"
        val importanceLevel = intent?.getStringExtra(TodoAlarmManager.IMPORTANCE_LEVEL) ?: "exist"

        if (taskDescription != "exist" && importanceLevel != "exist") {
            Log.i("NOTIFICATIONNN", "todoItem AlarmReceiver")
            notificationManager.get().createNotification(taskDescription, importanceLevel)
        }
    }
}