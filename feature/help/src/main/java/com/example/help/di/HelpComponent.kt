package com.example.help.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import com.example.core.di.Feature
import com.example.core.repositories.CategoryRepository
import com.example.help.screen.HelpFragment
import dagger.Component
import kotlin.properties.Delegates.notNull

@[Feature Component(dependencies = [HelpDeps::class])]
internal interface HelpComponent {
    fun inject(fragment: HelpFragment)

    @Component.Builder
    interface Builder {
        fun deps(helpDeps: HelpDeps): Builder

        fun build(): HelpComponent
    }

}

interface HelpDeps {
    val repository: CategoryRepository
}

interface HelpDepsProvider {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: HelpDeps

    companion object : HelpDepsProvider by HelpDepsStore
}

object HelpDepsStore : HelpDepsProvider {
    override var deps: HelpDeps by notNull()
}

internal class HelpComponentViewModel : ViewModel() {
    val helpComponent = DaggerHelpComponent.builder().deps(HelpDepsProvider.deps).build()
}
