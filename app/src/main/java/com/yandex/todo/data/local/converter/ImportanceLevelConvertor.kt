package com.yandex.todo.data.local.converter

import androidx.room.TypeConverter
import com.yandex.todo.domain.model.ImportanceLevel

class ImportanceLevelConvertor {
    @TypeConverter
    fun fromImportanceLevel(importanceLevel: ImportanceLevel): String {
        return importanceLevel.name
    }

    @TypeConverter
    fun fromImportanceLevel(importanceLevel: String): ImportanceLevel {
        return enumValueOf(importanceLevel)
    }
}