package com.yandex.todo.data.remote.notification

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import com.yandex.todo.data.receiver.AlarmReceiver
import com.yandex.todo.di.scope.AppScope
import com.yandex.todo.domain.model.TodoItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.util.Calendar
import java.util.Date
import javax.inject.Inject

@AppScope
class TodoAlarmManager @Inject constructor(
    private val applicationContext: Context
) : AlarmScheduler {
    private val alarmManager = applicationContext.getSystemService(
        Context.ALARM_SERVICE
    ) as AlarmManager

    override suspend fun schedule(todoItem: TodoItem) {
        withContext(Dispatchers.IO) {
            exactAlarmTask(todoItem)
        }
    }

    override suspend fun cancel(todoItem: TodoItem) {
        withContext(Dispatchers.IO) {
            alarmManager.cancel(getPendingIntentWithConstraint(todoItem))
        }
    }

    private fun exactAlarmTask(todoItem: TodoItem) {
        alarmManager.setExactAndAllowWhileIdle(
            AlarmManager.RTC_WAKEUP,
            getSchedulerTimeFromDeadline(),
            getPendingIntentWithConstraint(todoItem)
        )
    }

    private fun getPendingIntentWithConstraint(todoItem: TodoItem): PendingIntent? {
        val intent = Intent(applicationContext, AlarmReceiver::class.java).apply {
            putExtra(TASK_DESCRIPTION, todoItem.taskDescription)
            putExtra(IMPORTANCE_LEVEL, todoItem.importanceLevel.name)
        }

        return PendingIntent.getBroadcast(
            applicationContext,
            todoItem.hashCode(),
            intent,
            PendingIntent.FLAG_MUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )
    }

    private fun getSchedulerTimeFromDeadline(): Long {
        val calendar = Calendar.getInstance().apply {
            time = Date()
            add(Calendar.HOUR_OF_DAY, 12)
        }

        return calendar.timeInMillis
    }

    companion object {
        const val TASK_DESCRIPTION = "TASK_DESCRIPTION"
        const val IMPORTANCE_LEVEL = "IMPORTANCE_LEVEL"
    }
}

interface AlarmScheduler {
    suspend fun schedule(todoItem: TodoItem)
    suspend fun cancel(todoItem: TodoItem)
}