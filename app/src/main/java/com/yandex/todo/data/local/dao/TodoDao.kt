package com.yandex.todo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.yandex.todo.data.local.entity.TodoItemEntity

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos")
    fun getTodoListItems(): List<TodoItemEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun addTodoItem(todoItem: TodoItemEntity)

    @Delete
    fun deleteTodoItem(todoItem: TodoItemEntity)

    @Query("DELETE FROM todos")
    fun clearTodos()
}