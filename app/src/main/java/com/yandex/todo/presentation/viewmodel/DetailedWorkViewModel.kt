package com.yandex.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.todo.domain.model.ImportanceLevel
import com.yandex.todo.domain.model.TodoItem
import com.yandex.todo.domain.repository.TodoItemsRepository
import com.yandex.todo.presentation.event.DetailedWorkEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.UUID

class DetailedWorkViewModel(
    private val todoItemsRepository: TodoItemsRepository,
) : ViewModel() {
    private var _detailedWorkState = MutableStateFlow(DetailedState())
    val detailedWorkState = _detailedWorkState.asStateFlow()

    data class DetailedState(
        val description: String = "",
        val importanceLevel: ImportanceLevel = ImportanceLevel.LOW,
        val deadLine: String? = ""
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

            is DetailedWorkEvent.SetData -> {
                _detailedWorkState.value = _detailedWorkState.value.copy(
                    description = event.todoItem.taskDescription,
                    importanceLevel = event.todoItem.importanceLevel,
                    deadLine = getStringFromDate(event.todoItem.deadline)
                )
            }

            is DetailedWorkEvent.SaveData -> {
                viewModelScope.launch(Dispatchers.IO) {
                    todoItemsRepository.addTodoItem(
                        TodoItem(
                            id = UUID.randomUUID().toString(),
                            taskDescription = _detailedWorkState.value.description,
                            importanceLevel = _detailedWorkState.value.importanceLevel,
                            isDone = false,
                            createDate = Date(),
                            deadline = getDateFromString(_detailedWorkState.value.deadLine)
                        )
                    )
                }
            }

            is DetailedWorkEvent.UpdateData -> {
                viewModelScope.launch(Dispatchers.IO) {
                    todoItemsRepository.updateTodoItem(
                        TodoItem(
                            id = event.todoItem.id,
                            taskDescription = _detailedWorkState.value.description,
                            importanceLevel = _detailedWorkState.value.importanceLevel,
                            isDone = event.todoItem.isDone,
                            createDate = event.todoItem.createDate,
                            deadline = getDateFromString(_detailedWorkState.value.deadLine)
                        )
                    )
                }
            }

            is DetailedWorkEvent.RemoveData -> {
                viewModelScope.launch(Dispatchers.IO) {
                    todoItemsRepository.deleteTodoItem(event.todoItem)
                }
            }
        }
    }

    private fun getStringFromDate(date: Date?): String? {
        if (date == null) {
            return null
        }

        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.format(date)
    }

    private fun getDateFromString(date: String?): Date? {
        if (date == "") {
            return null
        }

        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        return format.parse(date)
    }
}