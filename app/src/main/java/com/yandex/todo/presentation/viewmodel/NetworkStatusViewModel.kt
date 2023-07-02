package com.yandex.todo.presentation.viewmodel

import android.net.ConnectivityManager
import android.net.Network
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.yandex.todo.domain.repository.TodoItemsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.launch

class NetworkStatusViewModel(
    private val todoItemsRepository: TodoItemsRepository,
    private val connectivityManager: ConnectivityManager
) : ViewModel() {
    private val _networkStatus = MutableStateFlow(true)
    val networkStatus = _networkStatus.asStateFlow()

    init {
        startNetworkStatusFlow()
    }

    private fun startNetworkStatusFlow() {
        viewModelScope.launch(Dispatchers.IO) {
            connectivityManager.createNetworkStatusFlow().collect { isConnected ->
                if (isConnected && !networkStatus.value) {
                    todoItemsRepository.mergeTodoItemList()
                    Log.i("TAG", "Зашел startNetworkStatusFlow")
                }

                _networkStatus.value = isConnected
            }
        }
    }

    private fun ConnectivityManager.createNetworkStatusFlow(): Flow<Boolean> = callbackFlow {
        val networkCallback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                trySend(true)
            }

            override fun onLost(network: Network) {
                trySend(false)
            }
        }

        registerDefaultNetworkCallback(networkCallback)

        awaitClose {
            unregisterNetworkCallback(networkCallback)
        }
    }
}