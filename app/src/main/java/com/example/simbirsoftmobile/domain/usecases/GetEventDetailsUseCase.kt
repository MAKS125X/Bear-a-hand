package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.domain.utils.extractResult
import javax.inject.Inject

class GetEventDetailsUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    operator fun invoke(eventId: String) =
        repository.getEventById(eventId)
            .extractResult()
}
