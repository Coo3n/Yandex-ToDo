package com.yandex.todo.di.module

import android.content.Context
import com.yandex.todo.MyApp
import dagger.Module
import dagger.Provides

@Module
class AppModule(private val application: MyApp) {
    @Provides
    fun provideContext(): Context {
        return application.applicationContext
    }
}