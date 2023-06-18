package com.yandex.todo.data.repository

import com.yandex.todo.data.local.dao.TodoDao
import com.yandex.todo.data.mapper.toTodoItem
import com.yandex.todo.data.mapper.toTodoItemEntity
import com.yandex.todo.domain.model.CreateTodoItem
import com.yandex.todo.domain.model.ListItem
import com.yandex.todo.domain.model.TodoItem
import com.yandex.todo.domain.repository.TodoItemsRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoItemsRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao
) : TodoItemsRepository {
    override suspend fun getTodoListItems(): Flow<List<ListItem>> {
        val todoList = todoDao.getTodoListItems()

        val convertedList: Flow<List<ListItem>> = todoList.map { todoItemList ->
            todoItemList.map { item -> item.toTodoItem() }
        }

        val finalTodoList = convertedList.map { list ->
            list + CreateTodoItem()
        }

        return finalTodoList
    }

    override suspend fun addTodoItem(todoItem: TodoItem) {
        todoDao.addTodoItem(todoItem.toTodoItemEntity())
    }

    override suspend fun deleteTodoItem(todoItem: TodoItem) {
        todoDao.deleteTodoItem(todoItem.toTodoItemEntity())
    }
}