package com.yandex.todo.di.module

import android.content.Context
import com.yandex.todo.data.local.TodoDataBase
import com.yandex.todo.data.local.dao.TodoDao
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class RoomModule {
    @Provides
    fun provideTodoDao(todoDataBase: TodoDataBase): TodoDao {
        return todoDataBase.todoDao()
    }

    @Provides
    @Singleton
    fun provideTodoDataBase(context: Context): TodoDataBase {
        return TodoDataBase.getTodoDataBaseInstance(context)
    }
}