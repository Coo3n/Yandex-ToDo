package com.yandex.todo.data.repository

import android.os.Build
import com.yandex.todo.data.local.dao.TodoDao
import com.yandex.todo.data.mapper.toTodoItemEntity
import com.yandex.todo.domain.model.CreateTodoItem
import com.yandex.todo.domain.model.ImportanceLevel
import com.yandex.todo.domain.model.TodoItem
import com.yandex.todo.domain.repository.TodoItemsRepository
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

class TodoItemsRepositoryImpl @Inject constructor(
    private val todoDao: TodoDao
) : TodoItemsRepository {
    override suspend fun getTodoListItems(): List<TodoItem> {
//        val todoList = todoDao.getTodoListItems()
//        return todoList.map { it.toTodoItem() }
        return listOf(
            TodoItem(
                id = "1",
                taskDescription = "sdfsdfsdffd",
                importanceLevel = ImportanceLevel.NO,
                isDone = true,
                createDate = Date()
            ),
            TodoItem(
                id = "5",
                taskDescription = "sdfsdfsdffd",
                importanceLevel = ImportanceLevel.NO,
                isDone = true,
                createDate = Date()
            ),
            TodoItem(
                id = "1",
                taskDescription = "sdfsdfsdffd",
                importanceLevel = ImportanceLevel.NO,
                isDone = true,
                createDate = Date()
            ),
        )
    }


    override suspend fun addTodoItem(todoItem: TodoItem) {
        todoDao.addTodoItem(todoItem.toTodoItemEntity())
    }

    override suspend fun deleteTodoItem(todoItem: TodoItem) {
        todoDao.deleteTodoItem(todoItem.toTodoItemEntity())
    }
}