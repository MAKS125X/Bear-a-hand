package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.EventModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class SearchEventsUseCase(
    private val repository: EventRepository = SimbirSoftApp.INSTANCE.appContainer.eventRepository,
) {
    operator fun invoke(query: String): Flow<Either<NetworkError, List<EventModel>>> =
        repository.getAllEvents()
            .map {
                when (it) {
                    is Either.Left -> it
                    is Either.Right -> Either.Right(
                        it.value
                            .filter { event ->
                                event.name.contains(
                                    query,
                                    true,
                                )
                            }
                    )
                }
            }
}
