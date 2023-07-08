package com.yandex.todo.di.module

import android.content.Context
import android.content.SharedPreferences
import android.net.ConnectivityManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.yandex.todo.MyApp
import com.yandex.todo.data.local.AccountManager
import dagger.Module
import dagger.Provides

@Module
object AppModule {
    @Provides
    fun provideMasterKey(context: Context): MasterKey {
        return MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build()
    }

    @Provides
    fun provideSharedPreferences(
        context: Context,
        masterKey: MasterKey
    ): SharedPreferences {
        return EncryptedSharedPreferences.create(
            context,
            "secret_shared_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    @Provides
    fun provideAccountManager(
        sharedPreferences: SharedPreferences
    ): AccountManager {
        return AccountManager(sharedPreferences)
    }

    @Provides
    fun provideConnectivityManager(context: Context): ConnectivityManager {
        return context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
    }
}