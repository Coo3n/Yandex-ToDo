package com.yandex.todo.data.mapper

import com.yandex.todo.data.local.entity.TodoItemEntity
import com.yandex.todo.domain.model.TodoItem

fun TodoItem.toTodoItemEntity(): TodoItemEntity {
    return TodoItemEntity(
        id = id,
        taskDescription = taskDescription,
        importanceLevel = importanceLevel,
        isDone = isDone,
        createDate = createDate
    )
}

fun TodoItemEntity.toTodoItem(): TodoItem {
    return TodoItem(
        id = id,
        taskDescription = taskDescription,
        importanceLevel = importanceLevel,
        isDone = isDone,
        createDate = createDate
    )
}
