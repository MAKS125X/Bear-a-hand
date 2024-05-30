package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.models.event.EventModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.domain.utils.extractResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchEventsUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    operator fun invoke(query: String): Flow<Either<DataError, List<EventModel>>> =
        repository.searchEventsByName("%$query%").extractResult()
}
