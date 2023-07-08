package com.yandex.todo.di.module

import android.content.Context
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.todo.di.scope.FragmentScope
import dagger.Module
import dagger.Provides

@Module
object YandexModule {
    @Provides
    @FragmentScope
    fun provideYandexAuthSdk(context: Context): YandexAuthSdk {
        return YandexAuthSdk(
            context,
            YandexAuthOptions(context)
        )
    }
}