package com.yandex.todo.presentation.event


sealed class ValidationAuthEvent {
    object Success : ValidationAuthEvent()
    object Failure : ValidationAuthEvent()
}