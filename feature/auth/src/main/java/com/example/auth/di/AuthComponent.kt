package com.example.auth.di

import androidx.annotation.RestrictTo
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.example.auth.screen.AuthFragment
import com.example.core.di.Feature
import dagger.Component
import kotlin.properties.Delegates.notNull

@[Feature Component(dependencies = [AuthDeps::class])]
internal interface AuthComponent {
    fun inject(fragment: AuthFragment)

    @Component.Builder
    interface Builder {

        fun deps(authNavigation: AuthDeps): Builder

        fun build(): AuthComponent
    }
}

interface AuthDeps {
    val authNavigation: AuthNavigation
}

interface AuthNavigation {
    fun onSuccessNavigation(fragmentManager: FragmentManager)
}

interface AuthDepsProvider {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: AuthDeps

    companion object : AuthDepsProvider by AuthDepsStore
}

object AuthDepsStore : AuthDepsProvider {
    override var deps: AuthDeps by notNull()
}

internal class AuthComponentViewModel : ViewModel() {
    val authComponent = DaggerAuthComponent.builder().deps(AuthDepsProvider.deps).build()
}
