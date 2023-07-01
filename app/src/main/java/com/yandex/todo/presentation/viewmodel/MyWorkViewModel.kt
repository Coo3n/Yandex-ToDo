package com.yandex.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.todo.domain.model.ListItem
import com.yandex.todo.domain.repository.TodoItemsRepository
import com.yandex.todo.presentation.event.MainWorkEvent
import com.yandex.todo.utils.Resource
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

class MyWorkViewModel(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    private var _todoList = MutableStateFlow(listOf<ListItem>())
    val todoList = _todoList.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing = _isRefreshing.asStateFlow()

    init {
        getTodoList(true)
    }

    fun onEvent(workEvent: MainWorkEvent) {
        when (workEvent) {
            is MainWorkEvent.Refresh -> {
                getTodoList(true)
            }
            is MainWorkEvent.Delete -> {
                viewModelScope.launch {
                    todoItemsRepository.deleteTodoItem(workEvent.todoItem)
                }
            }
        }
    }

    private fun getTodoList(fetchFromRemote: Boolean) {
        viewModelScope.launch {
            todoItemsRepository.getTodoListItems(fetchFromRemote)
                .distinctUntilChanged()
                .collect { result ->
                    when (result) {
                        is Resource.Loading -> {
                            _isRefreshing.value = result.isLoading
                        }
                        is Resource.Error -> {

                        }
                        is Resource.Success -> {
                            _todoList.value = result.data!!
                        }
                    }
                }
        }
    }
}
