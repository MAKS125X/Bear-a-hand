package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.EventModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.domain.utils.UnreadNewsController
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.onEach

class GetEventsBySettingsUseCase(
    private val repository: EventRepository = SimbirSoftApp.INSTANCE.appContainer.eventRepository,
) {
    operator fun invoke(vararg categoryIds: String): Flow<Either<NetworkError, List<EventModel>>> {
        val flow = if (categoryIds.isEmpty()) {
            repository.getAllEvents()
        } else {
            repository.getEventsByCategory(*categoryIds)
        }

        return flow
            .onEach {
                if (it is Either.Right) {
                    UnreadNewsController.emitUnreadValue(it.value)
                }
            }
    }
}
