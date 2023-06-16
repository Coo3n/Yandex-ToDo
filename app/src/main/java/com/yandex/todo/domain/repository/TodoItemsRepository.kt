package com.yandex.todo.domain.repository

import com.yandex.todo.domain.model.TodoItem

interface TodoItemsRepository {
    suspend fun getTodoListItems(): List<TodoItem>
    suspend fun addTodoItem(todoItem: TodoItem)
    suspend fun deleteTodoItem(todoItem: TodoItem)
}