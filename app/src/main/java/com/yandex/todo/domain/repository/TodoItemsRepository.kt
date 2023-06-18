package com.yandex.todo.domain.repository

import com.yandex.todo.domain.model.ListItem
import com.yandex.todo.domain.model.TodoItem
import kotlinx.coroutines.flow.Flow

interface TodoItemsRepository {
    suspend fun getTodoListItems(): Flow<List<ListItem>>
    suspend fun addTodoItem(todoItem: TodoItem)
    suspend fun deleteTodoItem(todoItem: TodoItem)
}