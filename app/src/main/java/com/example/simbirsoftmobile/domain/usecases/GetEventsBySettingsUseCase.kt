package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.EventModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.domain.utils.UnreadNewsController
import io.reactivex.rxjava3.core.Observable

class GetEventsBySettingsUseCase(
    private val repository: EventRepository = SimbirSoftApp.INSTANCE.appContainer.eventRepository
) {
    operator fun invoke(vararg categoryIds: String): Observable<Either<NetworkError, List<EventModel>>> {
        val observable = if (categoryIds.isEmpty()) {
            repository.getAllEvents()
        } else {
            repository.getEventsByCategory(*categoryIds)
        }

        return observable
            .doOnNext {
                if (it is Either.Right) {
                    UnreadNewsController.emitUnreadValue(it.value)
                }
            }
    }
}
