package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.EventModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx3.asFlow

class SearchOrganizationsUseCase(
    private val repository: EventRepository = SimbirSoftApp.INSTANCE.appContainer.eventRepository,
) {
    operator fun invoke(query: String): Flow<Either<NetworkError, List<EventModel>>> =
        repository.getAllEvents()
            .asFlow()
            .map {
                when (it) {
                    is Either.Left -> it
                    is Either.Right -> Either.Right(
                        it.value
                            .filter { event ->
                                event.organization.contains(
                                    query,
                                    true,
                                )
                            }
                            .distinctBy { event ->
                                event.organization
                            }
                    )
                }
            }
}
