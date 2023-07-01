package com.yandex.todo.di.module

import android.content.Context
import android.content.SharedPreferences
import com.yandex.authsdk.YandexAuthOptions
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.todo.data.local.AccountManager
import com.yandex.todo.data.local.dao.TodoDao
import com.yandex.todo.data.remote.TodoApi
import com.yandex.todo.data.remote.interceptor.TokenInterceptor
import com.yandex.todo.data.repository.TodoItemsRepositoryImpl
import com.yandex.todo.data.repository.YandexAuthRepositoryImpl
import com.yandex.todo.di.scope.CustomScope
import com.yandex.todo.domain.repository.TodoItemsRepository
import com.yandex.todo.domain.repository.YandexAuthRepository
import dagger.Binds
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
interface RemoteModule {
    companion object {
        @Provides
        fun provideGsonConverterFactory(): GsonConverterFactory {
            return GsonConverterFactory.create()
        }

        @Provides
        fun provideTokenInterceptor(accountManager: AccountManager): TokenInterceptor {
            return TokenInterceptor(accountManager)
        }

        @Provides
        fun provideOkHttpClient(tokenInterceptor: TokenInterceptor): OkHttpClient {
            return OkHttpClient.Builder()
                .addInterceptor(tokenInterceptor)
                .connectTimeout(100, TimeUnit.SECONDS)
                .readTimeout(100, TimeUnit.SECONDS)
                .build()
        }

        @Provides
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

        @Provides
        @CustomScope
        fun provideYandexAuthSdk(context: Context): YandexAuthSdk {
            return YandexAuthSdk(
                context,
                YandexAuthOptions(context)
            )
        }
    }

    @Binds
    fun provideYandexAuthRepository(
        yandexAuthRepository: YandexAuthRepositoryImpl
    ): YandexAuthRepository

    @Binds
    fun provideTodoItemRepository(
        todoItemsRepository: TodoItemsRepositoryImpl
    ): TodoItemsRepository
}