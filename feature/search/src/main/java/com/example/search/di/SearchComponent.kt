package com.example.search.di

import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import com.example.core.di.Feature
import com.example.core.repositories.EventRepository
import com.example.search.SearchFragment
import com.example.search.events.EventsFragment
import com.example.search.organizations.OrganizationsFragment
import dagger.Component
import kotlin.properties.Delegates.notNull

@[Feature Component(dependencies = [SearchDeps::class])]
internal interface SearchComponent {

    fun inject(fragment: SearchFragment)
    fun inject(fragment: EventsFragment)
    fun inject(fragment: OrganizationsFragment)

    @Component.Builder
    interface Builder {
        fun deps(newsDeps: SearchDeps): Builder

        fun build(): SearchComponent
    }
}

interface SearchDeps {
    val eventRepository: EventRepository
}

interface SearchCDepsProvider {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: SearchDeps

    companion object : SearchCDepsProvider by SearchCDepsStore
}

object SearchCDepsStore : SearchCDepsProvider {
    override var deps: SearchDeps by notNull()
}

internal class SearchComponentViewModel : ViewModel() {
    val searchComponent =
        DaggerSearchComponent.builder().deps(SearchCDepsProvider.deps).build()
}
