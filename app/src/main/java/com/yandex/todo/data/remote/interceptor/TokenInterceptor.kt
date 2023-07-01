package com.yandex.todo.data.remote.interceptor

import com.yandex.todo.data.local.AccountManager
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class TokenInterceptor @Inject constructor(
    private val accountManager: AccountManager
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
        val token = accountManager.getApiKey()
        val revision = accountManager.getRevision()
        request.addHeader("Authorization", "OAuth ${token.toString()}")
            .addHeader("X-Last-Known-Revision", revision.toString())
        return chain.proceed(request.build())
    }
}