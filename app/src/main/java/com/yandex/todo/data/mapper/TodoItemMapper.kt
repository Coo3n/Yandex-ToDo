package com.yandex.todo.data.mapper

import com.yandex.todo.data.local.entity.TodoItemEntity
import com.yandex.todo.data.remote.dto.TodoItemDto
import com.yandex.todo.data.remote.dto.TodoItemListRequest
import com.yandex.todo.data.remote.dto.TodoItemRequest
import com.yandex.todo.domain.model.TodoItem
import java.util.*

fun TodoItem.toTodoItemEntity(): TodoItemEntity {
    return TodoItemEntity(
        id = id,
        taskDescription = taskDescription,
        importanceLevel = importanceLevel,
        isDone = isDone,
        createDate = createDate,
        deadline = deadline
    )
}

fun TodoItem.toTodoItemRequest(): TodoItemRequest {
    return TodoItemRequest(
        todoItemDto = TodoItemDto(
            id = id,
            text = taskDescription,
            importanceLevel = importanceLevel.name.lowercase(),
            isDone = isDone,
            createDate = createDate.time,
            changedDate = Date().time,
            lastUpdatedDeviceId = "1",
            deadline = deadline?.time
        )
    )
}

fun TodoItemEntity.toTodoItemDto(): TodoItemDto {
    return TodoItemDto(
        id = id,
        text = taskDescription,
        importanceLevel = importanceLevel.name.lowercase(),
        isDone = isDone,
        createDate = createDate.time,
        changedDate = Date().time,
        lastUpdatedDeviceId = "1",
        deadline = deadline?.time
    )
}

fun TodoItemEntity.toTodoItem(): TodoItem {
    return TodoItem(
        id = id,
        taskDescription = taskDescription,
        importanceLevel = importanceLevel,
        isDone = isDone,
        createDate = createDate,
        deadline = deadline
    )
}


fun TodoItemDto.toTodoItem(): TodoItem {
    return TodoItem(
        id = id.toString(),
        taskDescription = text.toString(),
        importanceLevel = enumValueOf(importanceLevel.toString().uppercase()),
        isDone = isDone == true,
        createDate = Date(createDate!!),
        deadline = deadline?.let { Date(it) }
    )
}

fun TodoItemDto.toTodoItemEntity(): TodoItemEntity {
    return TodoItemEntity(
        id = id.toString(),
        taskDescription = text.toString(),
        importanceLevel = enumValueOf(importanceLevel.toString().uppercase()),
        isDone = isDone == true,
        createDate = Date(createDate!!),
        deadline = deadline?.let { Date(it) }
    )
}

