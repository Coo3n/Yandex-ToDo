package com.yandex.todo.data.local

import android.content.SharedPreferences
import javax.inject.Inject

class AccountManager @Inject constructor(
    private val sharedPreferences: SharedPreferences
) {
    companion object {
        const val API_KEY = "API_KEY"
        const val REVISION = "REVISION"
    }

    fun saveRevision(revision: String): AccountManager {
        sharedPreferences.edit()
            .putString(REVISION, revision)
            .apply()
        return this
    }

    fun getRevision(): String? = sharedPreferences.getString(REVISION, null)

    fun saveApiKey(apiKey: String): AccountManager {
        sharedPreferences.edit()
            .putString(API_KEY, apiKey)
            .apply()
        return this
    }

    fun getApiKey(): String? = sharedPreferences.getString(API_KEY, null)
}