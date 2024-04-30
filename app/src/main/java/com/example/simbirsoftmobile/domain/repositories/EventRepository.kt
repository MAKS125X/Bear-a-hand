package com.example.simbirsoftmobile.domain.repositories

import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.EventModel
import io.reactivex.rxjava3.core.Observable

interface EventRepository {
    fun getEventsByCategory(vararg categoryIds: String): Observable<Either<NetworkError, List<EventModel>>>

    fun getEventById(id: String): Observable<Either<NetworkError, EventModel>>

    fun getAllEvents(): Observable<Either<NetworkError, List<EventModel>>>
}
