package com.yandex.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.todo.domain.model.ListItem
import com.yandex.todo.domain.repository.TodoItemsRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyWorkViewModel @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    private var _todoList = MutableStateFlow(listOf<ListItem>())
    val todoList = _todoList.asStateFlow()

    init {
        getTodoList()
    }

    private fun getTodoList() {
        viewModelScope.launch {
            todoItemsRepository.getTodoListItems()
                .distinctUntilChanged()
                .collect { newList ->
                    _todoList.value = newList
                }
        }
    }
}
