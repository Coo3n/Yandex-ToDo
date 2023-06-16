package com.yandex.todo.domain.model

data class TodoItem(
    private val id: String,
    val taskDescription: String,
    val importanceLevel: ImportanceLevel,
    val isDone: Boolean,
    val createDate: String
) : ListItem {
    override fun getId(): String {
        return id
    }
}

enum class ImportanceLevel {
    LOW,
    NORMAL,
    URGENT
}