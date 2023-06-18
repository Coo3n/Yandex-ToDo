package com.yandex.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.todo.domain.model.ImportanceLevel
import com.yandex.todo.domain.model.TodoItem
import com.yandex.todo.domain.repository.TodoItemsRepository
import com.yandex.todo.presentation.event.DetailedWorkEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.util.*
import javax.inject.Inject

class DetailedWorkViewModel @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository
) : ViewModel() {
    private var _detailedWorkState = MutableStateFlow(DetailedState())
    val detailedWorkState = _detailedWorkState.asStateFlow()

    data class DetailedState(
        val description: String = "",
        val importanceLevel: ImportanceLevel = ImportanceLevel.NO,
        val deadLine: String = ""
    )

    fun onEvent(event: DetailedWorkEvent) {
        when (event) {
            is DetailedWorkEvent.OnChangedTextDescription -> {
                _detailedWorkState.value = _detailedWorkState.value.copy(
                    description = event.description
                )
            }
            is DetailedWorkEvent.OnChangedImportanceLevel -> {
                _detailedWorkState.value = _detailedWorkState.value.copy(
                    importanceLevel = event.importanceLevel
                )
            }
            is DetailedWorkEvent.OnChangedDeadLine -> {
                _detailedWorkState.value = _detailedWorkState.value.copy(
                    deadLine = event.deadLine
                )
            }
            is DetailedWorkEvent.SaveData -> {
                viewModelScope.launch {
                    todoItemsRepository.addTodoItem(
                        TodoItem(
                            id = Random(100).toString(),
                            taskDescription = _detailedWorkState.value.description,
                            importanceLevel = _detailedWorkState.value.importanceLevel,
                            isDone = false,
                            createDate = Date()
//                    createDate = _detailedWorkState.value.deadLine
                        )
                    )
                }
            }
            is DetailedWorkEvent.RemoveData -> {
                viewModelScope.launch {
                    todoItemsRepository.deleteTodoItem(event.todoItem)
                }
            }
        }
    }
}