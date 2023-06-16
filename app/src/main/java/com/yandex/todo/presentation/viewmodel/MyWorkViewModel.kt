package com.yandex.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.todo.domain.model.ListItem
import com.yandex.todo.domain.repository.TodoItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyWorkViewModel @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    private var _todoList = MutableStateFlow(mutableListOf<ListItem>())
    val todoList = _todoList.asStateFlow()

    init {
        getTodoList()
    }

    private fun getTodoList() {
        viewModelScope.launch {
            _todoList.value = todoItemsRepository.getTodoListItems().toMutableList()
        }
    }
}
