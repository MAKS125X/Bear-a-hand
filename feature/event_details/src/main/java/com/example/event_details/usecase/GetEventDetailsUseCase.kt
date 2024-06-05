package com.example.event_details.usecase

import com.example.core.repositories.EventRepository
import com.example.result.extractResult
import javax.inject.Inject

class GetEventDetailsUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    operator fun invoke(eventId: String) =
        repository.getEventById(eventId)
            .extractResult()
}
