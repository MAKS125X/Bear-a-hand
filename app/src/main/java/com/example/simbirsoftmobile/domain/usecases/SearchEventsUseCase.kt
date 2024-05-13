package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.models.EventModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.domain.utils.extractResult
import kotlinx.coroutines.flow.Flow

class SearchEventsUseCase(
    private val repository: EventRepository = SimbirSoftApp.INSTANCE.appContainer.eventRepository,
) {
    operator fun invoke(query: String): Flow<Either<DataError, List<EventModel>>> =
        repository.searchEventsByName("%$query%").extractResult()
}
