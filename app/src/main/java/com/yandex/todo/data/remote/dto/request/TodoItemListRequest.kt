package com.yandex.todo.data.remote.dto.request

import com.google.gson.annotations.SerializedName

data class TodoItemListRequest(
    @SerializedName("list")
    val tasks: List<TodoItemDto>
)
