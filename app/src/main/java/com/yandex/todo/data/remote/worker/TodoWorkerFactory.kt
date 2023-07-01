package com.yandex.todo.data.remote.worker

import android.content.Context
import androidx.work.ListenableWorker
import androidx.work.WorkerFactory
import androidx.work.WorkerParameters
import com.yandex.todo.domain.repository.TodoItemsRepository
import javax.inject.Inject

class TodoWorkerFactory @Inject constructor(
    private val todoItemsRepository: TodoItemsRepository
) : WorkerFactory() {
    override fun createWorker(
        appContext: Context,
        workerClassName: String,
        workerParameters: WorkerParameters
    ): ListenableWorker? {
        return when (workerClassName) {
            TodoWorker::class.java.name -> TodoWorker(appContext, workerParameters)
            else -> null
        }
    }
}