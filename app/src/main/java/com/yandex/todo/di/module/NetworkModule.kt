package com.yandex.todo.di.module

import com.yandex.todo.data.local.AccountManager
import com.yandex.todo.data.remote.TodoApi
import com.yandex.todo.data.remote.interceptor.TokenInterceptor
import com.yandex.todo.di.scope.AppScope
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

@Module
object NetworkModule {
    @Provides
    @AppScope
    fun provideGsonConverterFactory(): GsonConverterFactory {
        return GsonConverterFactory.create()
    }

    @Provides
    @AppScope
    fun provideTokenInterceptor(accountManager: AccountManager): TokenInterceptor {
        return TokenInterceptor(accountManager)
    }

    @Provides
    @AppScope
    fun provideOkHttpClient(tokenInterceptor: TokenInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(tokenInterceptor)
            .connectTimeout(100, TimeUnit.SECONDS)
            .readTimeout(100, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @AppScope
    fun provideTodoApi(
        okHttpClient: OkHttpClient,
        gsonConverterFactory: GsonConverterFactory
    ): TodoApi {
        return Retrofit.Builder()
            .baseUrl("https://beta.mrdekk.ru/todobackend/")
            .client(okHttpClient)
            .addConverterFactory(gsonConverterFactory)
            .build()
            .create(TodoApi::class.java)
    }
}