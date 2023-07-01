package com.yandex.todo.data.remote.dto

import com.google.gson.annotations.SerializedName
import com.yandex.todo.domain.model.TodoItem

data class TodoItemResponse(
    @SerializedName("element")
    val task: TodoItem,
    @SerializedName("revision")
    val revision: String,
    @SerializedName("status")
    val status: String
)
