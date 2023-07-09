package com.yandex.todo.di.module

import com.yandex.authsdk.YandexAuthSdk
import com.yandex.todo.data.repository.YandexAuthRepositoryImpl
import com.yandex.todo.di.scope.FragmentScope
import com.yandex.todo.domain.repository.YandexAuthRepository
import com.yandex.todo.presentation.viewmodel.AuthViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.Lazy

@Module
interface AuthModule {
    companion object {
        @Provides
        @FragmentScope
        fun provideAuthViewModuleFactory(
            authRepository: Lazy<YandexAuthRepository>,
            yandexAuthSdk: Lazy<YandexAuthSdk>
        ): AuthViewModelFactory {
            return AuthViewModelFactory(authRepository, yandexAuthSdk)
        }
    }

    @Binds
    @Reusable
    fun provideYandexAuthRepository(
        yandexAuthRepository: YandexAuthRepositoryImpl
    ): YandexAuthRepository
}