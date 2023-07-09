package com.yandex.todo

import android.app.Application
import android.util.Log
import androidx.work.*
import com.yandex.todo.data.remote.worker.TodoWorker
import com.yandex.todo.data.remote.worker.TodoWorkerFactory
import com.yandex.todo.di.component.AppComponent
import com.yandex.todo.di.component.DaggerAppComponent
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class MyApp : Application() {
    lateinit var appComponent: AppComponent

    @Inject
    lateinit var todoWorkerFactory: TodoWorkerFactory

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .factory()
            .create(this)

        appComponent.createAuthComponentFactory()
            .create()
            .injectMyApp(this)

        initBackgroundTodoWorker()
    }

    private fun initBackgroundTodoWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicWorkRequest = PeriodicWorkRequestBuilder<TodoWorker>(
            repeatInterval = 8,
            repeatIntervalTimeUnit = TimeUnit.HOURS,
//            flexTimeInterval = 0,
//            flexTimeIntervalUnit = TimeUnit.MINUTES
        ).setConstraints(constraints)
            .build()

        WorkManager.getInstance(applicationContext)
            .enqueueUniquePeriodicWork(
                "TodoWorker",
                ExistingPeriodicWorkPolicy.REPLACE,
                periodicWorkRequest
            )
    }
}