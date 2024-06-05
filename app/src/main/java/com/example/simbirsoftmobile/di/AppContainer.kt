package com.example.simbirsoftmobile.di

import android.content.Context
import androidx.fragment.app.FragmentManager
import com.example.core.di.AppScope
import com.example.core.repositories.CategoryRepository
import com.example.core.repositories.EventRepository
import com.example.data.di.DataModule
import com.example.event_details.di.EventDetailsDeps
import com.example.event_details.screen.EventDetailsFragment
import com.example.help.di.HelpDeps
import com.example.news.di.MoveToEventDetails
import com.example.news.di.NewsDeps
import com.example.simbirsoftmobile.R
import dagger.Binds
import dagger.BindsInstance
import dagger.Component
import dagger.Module
import javax.inject.Inject


@Component(modules = [AppModule::class, NewsDepsModule::class])
@AppScope
interface AppComponent : HelpDeps, NewsDeps, EventDetailsDeps {
    override val repository: CategoryRepository

    override val moveToEventDetails: MoveToEventDetails

    override val eventRepository: EventRepository

    @Component.Builder
    interface Builder {
        @BindsInstance
        fun context(context: Context): Builder

        fun build(): AppComponent
    }
}

val Context.appComponent: AppComponent
    get() = when (this) {
        is SimbirSoftApp -> appComponent
        else -> this.applicationContext.appComponent
    }

@Module(includes = [DataModule::class])
interface AppModule

class MoveToEventDetailsImp @Inject constructor() : MoveToEventDetails {
    override fun moveToEventDetails(fragmentManager: FragmentManager, id: String) {
        fragmentManager.beginTransaction().replace(
            R.id.fragmentContainerView,
            EventDetailsFragment.newInstance(id),
            EventDetailsFragment.TAG,
        ).addToBackStack(EventDetailsFragment.TAG).commit()
    }
}

@Module
interface NewsDepsModule : NewsDeps {
    override val categoryRepository: CategoryRepository
    override val eventRepository: EventRepository

    override val moveToEventDetails: MoveToEventDetails

    @Binds
    fun bindNavigation(moveToEventDetails: MoveToEventDetailsImp): MoveToEventDetails
}

