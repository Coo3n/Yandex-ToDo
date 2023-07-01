package com.yandex.todo.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.yandex.todo.data.local.converter.DateConvertor
import com.yandex.todo.data.local.converter.ImportanceLevelConvertor
import com.yandex.todo.data.local.dao.TodoDao
import com.yandex.todo.data.local.entity.TodoItemEntity

@Database(
    entities = [TodoItemEntity::class],
    version = 3,
    exportSchema = false
)
@TypeConverters(ImportanceLevelConvertor::class, DateConvertor::class)
abstract class TodoDataBase : RoomDatabase() {
    abstract fun todoDao(): TodoDao

    companion object {
        private var todoDataBase: TodoDataBase? = null

        fun getTodoDataBaseInstance(context: Context): TodoDataBase {
            return if (todoDataBase == null) {
                synchronized(this) {
                    Room.databaseBuilder(
                        context,
                        TodoDataBase::class.java,
                        "TodoDataBase"
                    ).fallbackToDestructiveMigration()
                        .allowMainThreadQueries()
                        .build()
                }
            } else {
                todoDataBase!!
            }
        }
    }
}