package com.example.event_details.di

import android.content.Context
import androidx.annotation.RestrictTo
import androidx.lifecycle.ViewModel
import androidx.work.ListenableWorker
import com.example.core.di.Feature
import com.example.core.repositories.EventRepository
import com.example.event_details.screen.EventDetailsFragment
import dagger.Component
import kotlin.properties.Delegates

@[Feature Component(dependencies = [EventDetailsDeps::class])]
internal interface EventDetailsComponent {

    fun inject(fragment: EventDetailsFragment)

    @Component.Builder
    interface Builder {
        fun deps(newsDeps: EventDetailsDeps): Builder

        fun build(): EventDetailsComponent
    }
}

interface EventDetailsDeps {
    val eventRepository: EventRepository
    val notificationDeps: WorkerDeps
}

interface WorkerDeps {
    val workerAction: (context: Context, String, String, Int) -> ListenableWorker.Result

    fun launchWorker(eventId: String, eventName: String, eventAmount: Int)
}

interface EventDetailsDepsProvider {
    @get:RestrictTo(RestrictTo.Scope.LIBRARY)
    val deps: EventDetailsDeps

    companion object : EventDetailsDepsProvider by EventDepsStore
}

object EventDepsStore : EventDetailsDepsProvider {
    override var deps: EventDetailsDeps by Delegates.notNull()
}

internal class EventDetailsComponentViewModel : ViewModel() {
    val eventDetailsComponent =
        DaggerEventDetailsComponent.builder().deps(EventDetailsDepsProvider.deps).build()
}
