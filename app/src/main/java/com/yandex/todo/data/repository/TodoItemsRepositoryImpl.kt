package com.yandex.todo.data.repository

import com.yandex.todo.data.local.dao.TodoDao
import com.yandex.todo.data.mapper.toTodoItemEntity
import com.yandex.todo.domain.model.TodoItem
import com.yandex.todo.domain.repository.TodoItemsRepository
import javax.inject.Inject

class TodoItemsRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao
) : TodoItemsRepository {
    override suspend fun getTodoListItems(): List<TodoItem> {
        TODO("Not yet implemented")
    }

    override suspend fun addTodoItem(todoItem: TodoItem) {
        todoDao.addTodoItem(todoItem = todoItem.toTodoItemEntity())
    }

    override suspend fun deleteTodoItem(todoItem: TodoItem) {
        todoDao.deleteTodoItem(todoItem = todoItem.toTodoItemEntity())
    }
}