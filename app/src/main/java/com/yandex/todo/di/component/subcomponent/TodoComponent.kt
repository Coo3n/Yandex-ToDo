package com.yandex.todo.di.component.subcomponent

import com.yandex.todo.MainActivity
import com.yandex.todo.data.remote.worker.TodoWorker
import com.yandex.todo.di.module.TodoModule
import com.yandex.todo.di.scope.FragmentScope
import com.yandex.todo.presentation.fragment.DetailedWorkFragment
import com.yandex.todo.presentation.fragment.MyWorkFragment
import dagger.Subcomponent

@Subcomponent(modules = [TodoModule::class])
@FragmentScope
interface TodoComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): TodoComponent
    }

    //Activity
    fun injectMainActivity(mainActivity: MainActivity)

    //Fragments
    fun injectMyWorkFragment(myWorkFragment: MyWorkFragment)
    fun injectDetailedWorkFragment(detailedWorkFragment: DetailedWorkFragment)

    //Worker
    fun injectWorker(todoWorker: TodoWorker)
}