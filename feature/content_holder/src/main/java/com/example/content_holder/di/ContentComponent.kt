package com.example.content_holder.di

import androidx.annotation.IdRes
import androidx.annotation.RestrictTo
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.ViewModel
import com.example.content_holder.screen.ContentFragment
import com.example.core.di.Feature
import com.example.core.repositories.CategoryRepository
import com.example.core.repositories.EventRepository
import dagger.Component
import kotlin.properties.Delegates.notNull

@[Feature Component(dependencies = [ContentDeps::class])]
internal interface ContentComponent {

    fun inject(fragment: ContentFragment)

    @Component.Builder
    interface Builder {
        fun deps(newsDeps: ContentDeps): Builder

        fun build(): ContentComponent
    }
}

interface ContentDeps {
    val contentHolderNavigation: ContentHolderNavigation
    val eventRepository: EventRepository
    val categoryRepository: CategoryRepository
}

interface ContentHolderNavigation {
    fun navigateToNews(@IdRes containerId: Int, fragmentManager: FragmentManager)
    fun navigateToHelp(@IdRes containerId: Int, fragmentManager: FragmentManager)
    fun navigateToProfile(@IdRes containerId: Int, fragmentManager: FragmentManager)
    fun navigateToSearch(@IdRes containerId: Int, fragmentManager: FragmentManager)
    fun navigateToHistory(@IdRes containerId: Int, fragmentManager: FragmentManager)
}

interface ContentDepsProvider {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: ContentDeps

    companion object : ContentDepsProvider by ContentDepsStore
}

object ContentDepsStore : ContentDepsProvider {
    override var deps: ContentDeps by notNull()
}

internal class ContentHolderComponentViewModel : ViewModel() {
    val newDetailsComponent =
        DaggerContentComponent.builder().deps(ContentDepsProvider.deps).build()
}
