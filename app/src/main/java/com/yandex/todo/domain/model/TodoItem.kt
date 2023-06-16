package com.yandex.todo.domain.model

import java.time.LocalDate
import java.util.Date

data class TodoItem(
    private val id: String,
    val taskDescription: String,
    val importanceLevel: ImportanceLevel,
    val isDone: Boolean,
    val createDate: Date
) : ListItem {
    override fun getId(): String {
        return id
    }
}

enum class ImportanceLevel {
    NO,
    LOW,
    TALL,
}