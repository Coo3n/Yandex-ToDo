package com.yandex.todo.di.component

import android.content.Context
import com.yandex.todo.MainActivity
import com.yandex.todo.data.remote.worker.TodoWorker
import com.yandex.todo.di.component.subcomponent.AuthComponent
import com.yandex.todo.di.component.subcomponent.TodoComponent
import com.yandex.todo.di.module.*
import com.yandex.todo.di.scope.AppScope
import dagger.BindsInstance
import dagger.Component

@Component(
    modules = [
        AppModule::class,
        LocalDataModule::class,
        NetworkModule::class
    ]
)
@AppScope
interface AppComponent {
    @Component.Factory
    interface Factory {
        fun create(@BindsInstance context: Context): AppComponent
    }

    //SubComponent
    fun createTodoComponentFactory(): TodoComponent.Factory
    fun createAuthComponentFactory(): AuthComponent.Factory
}