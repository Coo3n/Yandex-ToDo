package com.yandex.todo.data.local

import android.content.SharedPreferences
import dagger.Lazy
import javax.inject.Inject

class AccountManager @Inject constructor(
    private val sharedPreferences: Lazy<SharedPreferences>
) {
    companion object {
        const val API_KEY = "API_KEY"
        const val REVISION = "REVISION"
    }

    fun saveRevision(revision: String): AccountManager {
        sharedPreferences.get()
            .edit()
            .putString(REVISION, revision)
            .apply()
        return this
    }

    fun getRevision(): String? = sharedPreferences.get().getString(REVISION, null)

    fun saveApiKey(apiKey: String): AccountManager {
        sharedPreferences.get()
            .edit()
            .putString(API_KEY, apiKey)
            .apply()
        return this
    }

    fun getApiKey(): String? = sharedPreferences.get().getString(API_KEY, null)
}