package com.yandex.todo.data.remote.dto.response

import com.google.gson.annotations.SerializedName
import com.yandex.todo.data.remote.dto.request.TodoItemDto

data class TodoItemListResponse(
    @SerializedName("list")
    val tasks: List<TodoItemDto>,
    @SerializedName("revision")
    val revision: String,
    @SerializedName("status")
    val status: String
)