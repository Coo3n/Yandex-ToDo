package com.yandex.todo.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.yandex.todo.data.local.entity.TodoItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TodoDao {
    @Query("SELECT * FROM todos")
    fun getTodoListItems(): Flow<List<TodoItemEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodoItem(todoItem: TodoItemEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addTodoItemList(todoItemList: List<TodoItemEntity>)

    @Update
    suspend fun updateTodoItem(todoItem: TodoItemEntity)

    @Delete
    suspend fun deleteTodoItem(todoItem: TodoItemEntity)

    @Query("DELETE FROM todos")
    suspend fun clearTodos()
}