package com.yandex.todo.presentation.viewmodel

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.material.snackbar.Snackbar
import com.yandex.todo.domain.model.ListItem
import com.yandex.todo.domain.repository.TodoItemsRepository
import com.yandex.todo.presentation.event.MainWorkEvent
import com.yandex.todo.utils.Resource
import kotlinx.coroutines.Dispatchers
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

    private var removedItem: IndexedValue<ListItem>? = null

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

            is MainWorkEvent.TimingDelete -> {
                removeItem(workEvent.todoItem)
            }

            is MainWorkEvent.TimingRestore -> {
                undoRemoveItem()
            }
        }
    }

    private fun removeItem(item: ListItem) {
        val index = _todoList.value.indexOf(item)
        if (index != -1) {
            removedItem = IndexedValue(index, item)
            _todoList.value = _todoList.value - item
        }
    }

    private fun undoRemoveItem() {
        removedItem?.let { indexedItem ->
            val (index, item) = indexedItem
            val updatedList = _todoList.value.toMutableList()
            updatedList.add(index, item)
            _todoList.value = updatedList
            removedItem = null
        }
    }


    private fun getTodoList(fetchFromRemote: Boolean) {
        viewModelScope.launch(Dispatchers.IO) {
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
