package com.yandex.todo.data.repository

import android.content.Intent
import com.yandex.authsdk.YandexAuthException
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.authsdk.YandexAuthToken
import com.yandex.todo.data.local.AccountManager
import com.yandex.todo.domain.repository.YandexAuthRepository
import javax.inject.Inject
import dagger.Lazy

class YandexAuthRepositoryImpl @Inject constructor(
    private val yandexAuthSdk: Lazy<YandexAuthSdk>,
    private val accountManager: Lazy<AccountManager>
) : YandexAuthRepository {
    override suspend fun getYandexAuthToken(
        resultCode: Int, data: Intent?
    ): YandexAuthToken? {
        return try {
            val yandexAuthToken: YandexAuthToken? = yandexAuthSdk.get().extractToken(resultCode, data)
            if (yandexAuthToken != null) {
                accountManager.get().saveApiKey(yandexAuthToken.value)
            }
            return yandexAuthToken
        } catch (e: YandexAuthException) {
            null
        }
    }
}
