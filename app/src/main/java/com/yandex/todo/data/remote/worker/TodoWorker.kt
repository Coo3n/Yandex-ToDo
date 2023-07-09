package com.yandex.todo.data.remote.worker

import android.content.Context
import android.util.Log
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.yandex.todo.MyApp
import com.yandex.todo.domain.repository.TodoItemsRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import javax.inject.Inject


class TodoWorker(
    context: Context,
    params: WorkerParameters,
) : CoroutineWorker(context, params) {
    @Inject
    lateinit var todoItemsRepository: TodoItemsRepository

    init {
        (context.applicationContext as MyApp).appComponent
            .createTodoComponentFactory()
            .create()
            .injectWorker(this)
    }

    override suspend fun doWork(): Result {
        todoItemsRepository.updateTodoItemList()
        Log.i("Tag", "sd")
        return Result.success()
    }
}