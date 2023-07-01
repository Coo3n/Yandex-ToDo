package com.yandex.todo.data.repository

import android.content.Intent
import com.yandex.authsdk.YandexAuthException
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.authsdk.YandexAuthToken
import com.yandex.todo.data.local.AccountManager
import com.yandex.todo.domain.repository.YandexAuthRepository
import javax.inject.Inject

class YandexAuthRepositoryImpl @Inject constructor(
    private val yandexAuthSdk: YandexAuthSdk,
    private val accountManager: AccountManager
) : YandexAuthRepository {
    override suspend fun getYandexAuthToken(
        resultCode: Int, data: Intent?
    ): YandexAuthToken? {
        return try {
            val yandexAuthToken: YandexAuthToken? = yandexAuthSdk.extractToken(resultCode, data)
            if (yandexAuthToken != null) {
                accountManager.saveApiKey(yandexAuthToken.value)
            }
            return yandexAuthToken
        } catch (e: YandexAuthException) {
            null
        }
    }
}
