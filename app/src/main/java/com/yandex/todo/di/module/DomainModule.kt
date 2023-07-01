package com.yandex.todo.di.module

import android.content.SharedPreferences
import com.yandex.todo.data.local.AccountManager
import com.yandex.todo.domain.repository.TodoItemsRepository
import com.yandex.todo.presentation.viewmodel.TodoViewModelFactory
import dagger.Module
import dagger.Provides

@Module
object DomainModule {
    @Provides
    fun provideAccountManager(
        sharedPreferences: SharedPreferences
    ): AccountManager {
        return AccountManager(sharedPreferences)
    }
}