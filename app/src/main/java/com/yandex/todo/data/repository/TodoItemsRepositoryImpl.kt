package com.yandex.todo.data.repository

import android.util.Log
import androidx.work.impl.background.systemalarm.SystemAlarmScheduler
import com.yandex.todo.data.local.AccountManager
import com.yandex.todo.data.local.dao.TodoDao
import com.yandex.todo.data.mapper.toTodoItem
import com.yandex.todo.data.mapper.toTodoItemDto
import com.yandex.todo.data.mapper.toTodoItemEntity
import com.yandex.todo.data.mapper.toTodoItemRequest
import com.yandex.todo.data.remote.TodoApi
import com.yandex.todo.data.remote.dto.TodoItemListRequest
import com.yandex.todo.data.remote.notification.TodoAlarmManager
import com.yandex.todo.domain.model.CreateTodoItem
import com.yandex.todo.domain.model.ListItem
import com.yandex.todo.domain.model.TodoItem
import com.yandex.todo.domain.repository.TodoItemsRepository
import com.yandex.todo.utils.Resource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TodoItemsRepositoryImpl @Inject constructor(
    private val accountManager: AccountManager,
    private val todoScheduler: TodoAlarmManager,
    private val todoDao: TodoDao,
    private val todoApi: TodoApi
) : TodoItemsRepository {
    override suspend fun getTodoListItems(
        fetchFromRemote: Boolean
    ): Flow<Resource<List<ListItem>>> {
        return flow {
            try {
                emit(Resource.Loading(true)) // начало загрузки

                val todoList = todoDao.getTodoListItems()

                val convertedListItems: Flow<List<ListItem>> = todoList.map { todoItemList ->
                    todoItemList.map { item -> item.toTodoItem() }
                }

                val lastElementTodoList = CreateTodoItem()
                val finalTodoListItems = convertedListItems.map { convertedListItems ->
                    convertedListItems + lastElementTodoList
                }.first()

                emit(Resource.Success(finalTodoListItems)) // отсылаем локальные данные

                val shouldJustLoadOnCache = finalTodoListItems.isNotEmpty() && !fetchFromRemote
                if (shouldJustLoadOnCache) { // берем с кэша или с сервера?
                    emit(Resource.Loading(false))
                    return@flow
                }

                val responseApi = todoApi.getTodoList()
                if (responseApi.isSuccessful) {
                    accountManager.saveRevision(responseApi.body()?.revision.toString())

                    emit(Resource.Success(
                        data = responseApi.body()?.listTodoItem!!.map { it.toTodoItem() } + lastElementTodoList
                    ))

                    todoDao.clearTodos()
                    todoDao.addTodoItemList(
                        todoItemList = responseApi.body()?.listTodoItem!!.map { it.toTodoItemEntity() }
                    )
                } else {
                    emit(Resource.Error("Произошла ошибка :)"))
                }
            } catch (exception: Exception) {
                emit(Resource.Error(exception.message.toString()))
            } finally {
                emit(Resource.Loading(false))
            }
        }
    }

    override suspend fun mergeTodoItemList() {
        try {
            val convertedLocalTodoList = todoDao.getTodoListItems()
                .first()
                .map { it.toTodoItemDto() }

            val resultApi = todoApi.mergeTodoList(
                TodoItemListRequest(tasks = convertedLocalTodoList)
            )

            if (resultApi.isSuccessful) {
                accountManager.saveRevision(resultApi.body()?.revision.toString())
            }
        } catch (exception: Exception) {
            Log.i("mergeTodoItemList", exception.message.toString())
        }
    }

    override suspend fun addTodoItem(todoItem: TodoItem) {
        try {
            todoDao.addTodoItem(todoItem.toTodoItemEntity())

            if (todoItem.deadline != null) {
                todoScheduler.schedule(todoItem)
            }

            val resultApi = todoApi.addTodoItem(todoItem.toTodoItemRequest())
            if (resultApi.isSuccessful) {
                accountManager.saveRevision(resultApi.body()?.revision.toString())
            }
        } catch (exception: Exception) {
            Log.i("addTodoItem", exception.message.toString())
        }
    }

    override suspend fun updateTodoItem(todoItem: TodoItem) {
        try {
            Log.i("updateTodoItem", "updateTodoItem")
            todoDao.updateTodoItem(todoItem.toTodoItemEntity())
            todoScheduler.schedule(todoItem)
            val resultApi = todoApi.updateTodoItem(
                todoItem.id,
                todoItem.toTodoItemRequest()
            )

            if (resultApi.isSuccessful) {
                accountManager.saveRevision(resultApi.body()?.revision.toString())
            }

        } catch (exception: Exception) {
            Log.i("updateTodoItem", exception.message.toString())
        }
    }

    override suspend fun updateTodoItemList() {
        try {
            Log.i("updateTodoItemLIST", "updateTodoItemLIST")
            val responseApi = todoApi.getTodoList()
            if (responseApi.isSuccessful) {
                accountManager.saveRevision(responseApi.body()?.revision.toString())

                val listTodoItems = responseApi.body()?.listTodoItem!!

                todoDao.clearTodos()
                todoDao.addTodoItemList(
                    todoItemList = listTodoItems.map { it.toTodoItemEntity() }
                )

                listTodoItems.forEach { itemDto ->
                    if (itemDto.deadline != null) {
                        todoScheduler.schedule(itemDto.toTodoItem())
                    }
                }
            }
        } catch (exception: Exception) {
            Log.i("updateTodoItemList", exception.message.toString())
        }
    }

    override suspend fun deleteTodoItem(todoItem: TodoItem) {
        try {
            todoDao.deleteTodoItem(todoItem.toTodoItemEntity())

            if (todoItem.deadline != null) {
                todoScheduler.cancel(todoItem)
            }

            val resultApi = todoApi.deleteTodoItemById(todoItem.id)
            if (resultApi.isSuccessful) {
                accountManager.saveRevision(resultApi.body()?.revision.toString())
            }
        } catch (exception: Exception) {
            Log.i("deleteTodoItem", exception.message.toString())
        }
    }
}