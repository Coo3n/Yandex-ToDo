package com.yandex.todo.data.local

import android.app.UiModeManager
import android.content.Context
import androidx.appcompat.app.AppCompatDelegate

class ThemeManager(private val context: Context) {
    fun getSystemTheme(): Int {
        val uiModeManager = context.getSystemService(
            Context.UI_MODE_SERVICE
        ) as UiModeManager

        return when (uiModeManager.nightMode) {
            UiModeManager.MODE_NIGHT_YES -> AppCompatDelegate.MODE_NIGHT_YES
            UiModeManager.MODE_NIGHT_NO -> AppCompatDelegate.MODE_NIGHT_NO
            else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
        }
    }

    fun getTheme(): Int {
        return context.getSharedPreferences(
            THEME_PREFS_NAME,
            Context.MODE_PRIVATE
        ).getInt(KEY_THEME, -1)
    }

    fun setTheme(theme: Int) {
        context.getSharedPreferences(THEME_PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putInt(KEY_THEME, theme)
            .apply()
    }

    companion object {
        private const val THEME_PREFS_NAME = "THEME_PREFS_NAME"
        private const val KEY_THEME = "SELECTED_THEME"
    }
}