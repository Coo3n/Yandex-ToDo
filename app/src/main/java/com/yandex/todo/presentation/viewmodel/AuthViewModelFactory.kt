package com.yandex.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.todo.domain.repository.YandexAuthRepository
import javax.inject.Inject

class AuthViewModelFactory @Inject constructor(
    private var authRepository: YandexAuthRepository,
    private val yandexAuthSdk: YandexAuthSdk
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthYandexViewModel::class.java) -> {
                AuthYandexViewModel(authRepository, yandexAuthSdk) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}