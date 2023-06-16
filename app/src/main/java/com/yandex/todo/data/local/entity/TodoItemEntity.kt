package com.yandex.todo.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.yandex.todo.domain.model.ImportanceLevel
import java.util.Date

@Entity(tableName = "todos")
data class TodoItemEntity(
    @PrimaryKey val id: String,
    val taskDescription: String,
    val importanceLevel: ImportanceLevel,
    val isDone: Boolean,
    val createDate: Date
)