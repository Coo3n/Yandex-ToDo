package com.yandex.todo.di.module

import com.yandex.todo.data.local.dao.TodoDao
import com.yandex.todo.data.repository.TodoItemsRepositoryImpl
import com.yandex.todo.domain.repository.TodoItemsRepository
import com.yandex.todo.presentation.viewmodel.TodoViewModelFactory
import dagger.Module
import dagger.Provides

@Module
class DomainModule {
    @Provides
    fun provideTodoItemRepository(todoDao: TodoDao): TodoItemsRepository {
        return TodoItemsRepositoryImpl(todoDao)
    }

    @Provides
    fun provideTodoViewModelFactory(
        todoItemsRepository: TodoItemsRepository
    ): TodoViewModelFactory {
        return TodoViewModelFactory(todoItemsRepository)
    }
}