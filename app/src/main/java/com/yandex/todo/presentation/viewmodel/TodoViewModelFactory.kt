package com.yandex.todo.presentation.viewmodel

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yandex.todo.domain.repository.TodoItemsRepository
import dagger.Lazy
import javax.inject.Inject

class TodoViewModelFactory @Inject constructor(
    private var todoItemsRepository: Lazy<TodoItemsRepository>,
    private val connectivityManager: Lazy<ConnectivityManager>
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MyWorkViewModel::class.java) -> {
                MyWorkViewModel(todoItemsRepository.get()) as T
            }
            modelClass.isAssignableFrom(DetailedWorkViewModel::class.java) -> {
                DetailedWorkViewModel(todoItemsRepository.get()) as T
            }
            modelClass.isAssignableFrom(NetworkStatusViewModel::class.java) -> {
                NetworkStatusViewModel(todoItemsRepository.get(), connectivityManager.get()) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}