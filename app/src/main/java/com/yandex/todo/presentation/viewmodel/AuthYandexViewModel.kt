package com.yandex.todo.presentation.viewmodel

import android.content.Intent
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.authsdk.YandexAuthException
import com.yandex.authsdk.YandexAuthLoginOptions
import com.yandex.authsdk.YandexAuthSdk
import com.yandex.authsdk.YandexAuthToken
import com.yandex.todo.domain.repository.YandexAuthRepository
import com.yandex.todo.presentation.event.ValidationAuthEvent
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

class AuthYandexViewModel(
    private val authRepository: YandexAuthRepository,
    private val yandexAuthSdk: YandexAuthSdk
) : ViewModel() {
    private val _validationEventChannel = Channel<ValidationAuthEvent>()
    val validationEventChannel = _validationEventChannel.receiveAsFlow()

    fun startYandexAuthLogin(yandexAuthLauncher: ActivityResultLauncher<Intent>) {
        val loginOptionsBuilder = YandexAuthLoginOptions.Builder()
        val intent: Intent = yandexAuthSdk.createLoginIntent(loginOptionsBuilder.build())
        yandexAuthLauncher.launch(intent)
    }

    fun handleYandexAuthResult(resultCode: Int, data: Intent?) {
        viewModelScope.launch(Dispatchers.IO) {
            val yandexAuthToken = authRepository.getYandexAuthToken(resultCode, data)
            _validationEventChannel.send(
                if (yandexAuthToken != null) {
                    ValidationAuthEvent.Success
                } else {
                    ValidationAuthEvent.Failure
                }
            )
        }
    }
}


