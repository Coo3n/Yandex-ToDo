package com.yandex.todo.di.module

import android.content.Context
import com.yandex.todo.data.local.TodoDataBase
import com.yandex.todo.data.local.dao.TodoDao
import com.yandex.todo.di.scope.AppScope
import dagger.Module
import dagger.Provides

@Module
object LocalDataModule {
    @Provides
    fun provideTodoDao(todoDataBase: TodoDataBase): TodoDao {
        return todoDataBase.todoDao()
    }

    @Provides
    @AppScope
    fun provideTodoDataBase(context: Context): TodoDataBase {
        return TodoDataBase.getTodoDataBaseInstance(context)
    }
}