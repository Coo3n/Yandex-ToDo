package com.yandex.todo.domain.repository

import android.content.Intent
import com.yandex.authsdk.YandexAuthToken

interface YandexAuthRepository {
    suspend fun getYandexAuthToken(resultCode: Int, data: Intent?): YandexAuthToken?
}