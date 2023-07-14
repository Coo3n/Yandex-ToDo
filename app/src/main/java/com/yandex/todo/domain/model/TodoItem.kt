package com.yandex.todo.domain.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize
import java.util.Date

@Parcelize
data class TodoItem(
    override val id: String,
    val taskDescription: String,
    val importanceLevel: ImportanceLevel,
    val isDone: Boolean,
    val createDate: Date,
    val deadline: Date? = null,
) : ListItem(), Parcelable

enum class ImportanceLevel {
    LOW,
    BASIC,
    IMPORTANT,
}