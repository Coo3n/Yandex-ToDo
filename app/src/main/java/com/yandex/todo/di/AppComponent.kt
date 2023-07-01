package com.yandex.todo.di

import com.yandex.todo.MainActivity
import com.yandex.todo.data.remote.worker.TodoWorker
import com.yandex.todo.di.module.AppModule
import com.yandex.todo.di.module.DomainModule
import com.yandex.todo.di.module.RemoteModule
import com.yandex.todo.di.module.RoomModule
import com.yandex.todo.di.scope.CustomScope
import com.yandex.todo.presentation.fragment.AuthYandexFragment
import com.yandex.todo.presentation.fragment.DetailedWorkFragment
import com.yandex.todo.presentation.fragment.MyWorkFragment
import dagger.Component
import javax.inject.Singleton

@Component(modules = [AppModule::class, DomainModule::class, RoomModule::class, RemoteModule::class])
@CustomScope
interface AppComponent {
    //Activity
    fun injectMainActivity(mainActivity: MainActivity)

    //Fragments
    fun injectMyWorkFragment(myWorkFragment: MyWorkFragment)
    fun injectDetailedWorkFragment(detailedWorkFragment: DetailedWorkFragment)
    fun injectAuthYandexFragment(authYandexFragment: AuthYandexFragment)

    //Worker
    fun injectWorker(todoWorker: TodoWorker)
}