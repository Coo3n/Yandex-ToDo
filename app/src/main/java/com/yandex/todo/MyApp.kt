package com.yandex.todo

import android.app.Application
import com.yandex.todo.di.AppComponent
import com.yandex.todo.di.DaggerAppComponent
import com.yandex.todo.di.module.AppModule

class MyApp : Application() {
    lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        appComponent = DaggerAppComponent.builder()
            .appModule(AppModule(this))
            .build()
    }
}