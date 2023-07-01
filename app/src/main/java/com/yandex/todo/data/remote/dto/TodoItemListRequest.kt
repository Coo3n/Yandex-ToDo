package com.yandex.todo.data.remote.dto

import com.google.gson.annotations.SerializedName

data class TodoItemListRequest(
    @SerializedName("list")
    val tasks: List<TodoItemDto>
)
