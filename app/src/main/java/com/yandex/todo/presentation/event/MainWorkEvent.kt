package com.yandex.todo.presentation.event

import com.yandex.todo.domain.model.TodoItem

sealed class MainWorkEvent {
    object Refresh : MainWorkEvent()
    data class Delete(val todoItem: TodoItem) : MainWorkEvent()
}
