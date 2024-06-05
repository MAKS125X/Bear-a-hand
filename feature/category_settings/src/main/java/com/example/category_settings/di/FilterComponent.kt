package com.example.category_settings.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import com.example.category_settings.screen.FilterFragment
import com.example.core.di.Feature
import com.example.core.repositories.CategoryRepository
import dagger.Component
import kotlin.properties.Delegates.notNull

@[Feature Component(dependencies = [FilterDeps::class])]
internal interface FilterComponent {

    fun inject(fragment: FilterFragment)

    @Component.Builder
    interface Builder {
        fun deps(filterDeps: FilterDeps): Builder

        fun build(): FilterComponent
    }
}

interface FilterDeps {
    val categoryRepository: CategoryRepository
}

interface FilterDepsProvider {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: FilterDeps

    companion object : FilterDepsProvider by FilterDepsStore
}

object FilterDepsStore : FilterDepsProvider {
    override var deps: FilterDeps by notNull()
}

internal class FilterComponentViewModel : ViewModel() {
    val newDetailsComponent =
        DaggerFilterComponent.builder().deps(FilterDepsProvider.deps).build()
}