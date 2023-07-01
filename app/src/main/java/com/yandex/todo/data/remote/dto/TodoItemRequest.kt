package com.yandex.todo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TodoItemRequest(
    @SerializedName("element")
    val todoItemDto: TodoItemDto
)