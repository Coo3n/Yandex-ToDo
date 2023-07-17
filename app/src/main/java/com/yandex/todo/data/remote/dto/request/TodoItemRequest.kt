package com.yandex.todo.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class TodoItemRequest(
    @SerializedName("element")
    val todoItemDto: TodoItemDto
)