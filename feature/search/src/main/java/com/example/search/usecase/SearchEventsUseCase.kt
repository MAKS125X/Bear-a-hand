package com.example.search.usecase

import com.example.core.models.event.EventModel
import com.example.core.repositories.EventRepository
import com.example.result.DataError
import com.example.result.Either
import com.example.result.extractResult
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SearchEventsUseCase @Inject constructor(
    private val repository: EventRepository,
) {
    operator fun invoke(query: String): Flow<Either<DataError, List<EventModel>>> =
        repository.searchEventsByName("%$query%").extractResult()
}
