package com.yandex.todo

import android.app.Application
import com.yandex.todo.di.component.AppComponent
import com.yandex.todo.di.component.DaggerAppComponent

class MyApp : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent
            .factory()
            .create(this)
    }
}