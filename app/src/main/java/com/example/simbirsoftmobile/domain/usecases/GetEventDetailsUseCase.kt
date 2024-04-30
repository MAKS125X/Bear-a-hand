package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.domain.utils.UnreadNewsController

class GetEventDetailsUseCase(
    private val repository: EventRepository =  SimbirSoftApp.INSTANCE.appContainer.eventRepository
) {
    operator fun invoke(eventId: String) = repository.getEventById(eventId).doOnNext {
        if (it is Either.Right) {
            UnreadNewsController.addReadValue(it.value)
        }
    }
}
