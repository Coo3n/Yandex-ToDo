package com.yandex.todo.data.remote.dto

import com.google.gson.annotations.SerializedName
import java.util.*

data class ListTodoItemDto(
    @SerializedName("status")
    val status: String,
    @SerializedName("revision")
    val revision: String,
    @SerializedName("list")
    val listTodoItem: List<TodoItemDto>
)

data class TodoItemDto(
    @SerializedName("id")
    val id: String?,
    @SerializedName("text")
    val text: String?,
    @SerializedName("importance")
    val importanceLevel: String?,
    @SerializedName("done")
    val isDone: Boolean?,
    @SerializedName("color")
    val color: String? = null,
    @SerializedName("created_at")
    val createDate: Long?,
    @SerializedName("changed_at")
    val changedDate: Long?,
    @SerializedName("last_updated_by")
    val lastUpdatedDeviceId: String?,
)