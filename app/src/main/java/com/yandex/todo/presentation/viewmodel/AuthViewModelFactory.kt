package com.yandex.todo.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.todo.domain.repository.YandexAuthRepository
import javax.inject.Inject
import dagger.Lazy

class AuthViewModelFactory @Inject constructor(
    private var authRepository: Lazy<YandexAuthRepository>,
    private val yandexAuthSdk: Lazy<YandexAuthSdk>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(AuthYandexViewModel::class.java) -> {
                AuthYandexViewModel(authRepository.get(), yandexAuthSdk.get()) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}