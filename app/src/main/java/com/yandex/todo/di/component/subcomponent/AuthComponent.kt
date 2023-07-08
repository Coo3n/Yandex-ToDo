package com.yandex.todo.di.component.subcomponent

import com.yandex.todo.di.module.AuthModule
import com.yandex.todo.di.module.YandexModule
import com.yandex.todo.di.scope.FragmentScope
import com.yandex.todo.presentation.fragment.AuthYandexFragment
import dagger.Subcomponent

@Subcomponent(
    modules = [
        AuthModule::class,
        YandexModule::class,
    ]
)
@FragmentScope
interface AuthComponent {
    @Subcomponent.Factory
    interface Factory {
        fun create(): AuthComponent
    }

    fun injectAuthYandexFragment(authYandexFragment: AuthYandexFragment)
}