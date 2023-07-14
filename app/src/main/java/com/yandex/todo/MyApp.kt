package com.yandex.todo

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.yandex.todo.data.local.ThemeManager
import com.yandex.todo.data.remote.worker.TodoWorker
import com.yandex.todo.di.component.AppComponent
import com.yandex.todo.di.component.DaggerAppComponent
import java.util.concurrent.TimeUnit

class MyApp : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .factory()
            .create(this)

        setApplicationTheme()
        initBackgroundTodoWorker()
    }

    private fun setApplicationTheme() {
        AppCompatDelegate.setDefaultNightMode(
            ThemeManager(this).getTheme()
        )
    }

    private fun initBackgroundTodoWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<TodoWorker>(
            repeatInterval = 8,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
            flexTimeInterval = 0,
            flexTimeIntervalUnit = TimeUnit.MINUTES
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "TodoWorker",
                ExistingPeriodicWorkPolicy.KEEP,
                periodicWorkRequest
            )
    }
}