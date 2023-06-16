package com.yandex.todo.presentation.event

import com.yandex.todo.domain.model.ImportanceLevel

sealed class DetailedWorkEvent {
    data class OnChangedTextDescription(val description: String) : DetailedWorkEvent()
    data class OnChangedImportanceLevel(val importanceLevel: ImportanceLevel) : DetailedWorkEvent()
    data class OnChangedDeadLine(val deadLine: String) : DetailedWorkEvent()
    object SaveData: DetailedWorkEvent()
    object RemoveData: DetailedWorkEvent()
}
