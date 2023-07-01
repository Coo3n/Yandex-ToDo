package com.yandex.todo.presentation.viewmodel

import android.net.ConnectivityManager
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.yandex.todo.domain.repository.TodoItemsRepository
import javax.inject.Inject

class TodoViewModelFactory @Inject constructor(
    private var todoItemsRepository: TodoItemsRepository,
    private val connectivityManager: ConnectivityManager
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MyWorkViewModel::class.java) -> {
                MyWorkViewModel(todoItemsRepository) as T
            }
            modelClass.isAssignableFrom(DetailedWorkViewModel::class.java) -> {
                DetailedWorkViewModel(todoItemsRepository) as T
            }
            modelClass.isAssignableFrom(NetworkStatusViewModel::class.java) -> {
                NetworkStatusViewModel(todoItemsRepository, connectivityManager) as T
            }
            else -> {
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }
}