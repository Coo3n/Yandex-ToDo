package com.yandex.todo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TodoItemListResponse(
    @SerializedName("list")
    val tasks: List<TodoItemDto>,
    @SerializedName("revision")
    val revision: String,
    @SerializedName("status")
    val status: String
)