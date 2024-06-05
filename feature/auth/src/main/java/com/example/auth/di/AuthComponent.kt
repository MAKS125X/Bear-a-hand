package com.example.auth.di

import com.example.auth.screen.AuthFragment
import dagger.Component
import kotlin.properties.Delegates.notNull

@[Component(dependencies = [AuthNavigation::class])]
internal interface AuthComponent {
    fun inject(fragment: AuthFragment)

    @Component.Builder
    interface Builder {

        fun deps(authNavigation: AuthNavigation): Builder

        fun build(): AuthComponent
    }
}

fun interface AuthNavigation {
    fun goToContent()
}

interface AuthDepsProvider {
    val deps: AuthNavigation

    companion object : AuthDepsProvider by AuthDepsStore
}

object AuthDepsStore : AuthDepsProvider {
    override var deps: AuthNavigation by notNull()

}