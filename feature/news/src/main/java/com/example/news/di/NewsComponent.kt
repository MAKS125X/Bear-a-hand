package com.example.news.di

import androidx.annotation.RestrictTo
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.example.core.di.Feature
import com.example.core.repositories.CategoryRepository
import com.example.core.repositories.EventRepository
import com.example.news.screen.NewsFragment
import dagger.Component
import kotlin.properties.Delegates.notNull

@[Feature Component(dependencies = [NewsDeps::class])]
internal interface NewsComponent {

    fun inject(fragment: NewsFragment)

    @Component.Builder
    interface Builder {
        fun deps(newsDeps: NewsDeps): Builder

        fun build(): NewsComponent
    }
}

interface NewsDeps {
    val newsComponentNavigation: NewsComponentNavigation
    val eventRepository: EventRepository
    val categoryRepository: CategoryRepository
}

interface NewsComponentNavigation {
    fun navigateToEventDetails(
        fragmentManager: FragmentManager,
        id: String
    )

    fun navigateToSettings(fragmentManager: FragmentManager)
}

interface NewsDepsProvider {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: NewsDeps

    companion object : NewsDepsProvider by NewsDepsStore
}

object NewsDepsStore : NewsDepsProvider {
    override var deps: NewsDeps by notNull()
}

internal class NewsComponentViewModel : ViewModel() {
    val newDetailsComponent =
        DaggerNewsComponent.builder().deps(NewsDepsProvider.deps).build()
}