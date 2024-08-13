package com.example.profile.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import com.example.core.di.Feature
import com.example.core.repositories.SettingsRepository
import com.example.profile.screen.ProfileFragment
import dagger.Component
import kotlin.properties.Delegates

@[Feature Component(dependencies = [ProfileDeps::class])]
internal interface ProfileComponent {

    fun inject(fragment: ProfileFragment)

    @Component.Builder
    interface Builder {
        fun deps(profileDeps: ProfileDeps): Builder

        fun build(): ProfileComponent
    }
}

interface ProfileDeps {
    val settingsRepository: SettingsRepository
}

interface ProfileDepsProvider {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: ProfileDeps

    companion object : ProfileDepsProvider by ProfileDepsStore
}

object ProfileDepsStore : ProfileDepsProvider {
    override var deps: ProfileDeps by Delegates.notNull()
}

internal class ProfileComponentViewModel : ViewModel() {
    val profileComponent =
        DaggerProfileComponent.builder().deps(ProfileDepsProvider.deps).build()
}
