package com.yandex.todo.di.module

import android.net.ConnectivityManager
import com.yandex.todo.data.repository.TodoItemsRepositoryImpl
import com.yandex.todo.di.scope.FragmentScope
import com.yandex.todo.domain.repository.TodoItemsRepository
import com.yandex.todo.presentation.viewmodel.TodoViewModelFactory
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.Reusable
import dagger.Lazy

@Module
interface TodoModule {
    companion object {
        @Provides
        @FragmentScope
        fun provideTodoViewModelFactory(
            todoItemsRepository: Lazy<TodoItemsRepository>,
            connectivityManager: Lazy<ConnectivityManager>
        ): TodoViewModelFactory {
            return TodoViewModelFactory(todoItemsRepository, connectivityManager)
        }
    }

    @Binds
    @Reusable
    fun provideTodoItemRepository(
        todoItemsRepository: TodoItemsRepositoryImpl
    ): TodoItemsRepository
}