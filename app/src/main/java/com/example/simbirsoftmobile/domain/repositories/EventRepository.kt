package com.example.simbirsoftmobile.domain.repositories

import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.EventModel
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEventsByCategory(vararg categoryIds: String): Flow<Either<NetworkError, List<EventModel>>>

    fun getEventById(id: String): Flow<Either<NetworkError, EventModel>>

    fun getAllEvents(): Flow<Either<NetworkError, List<EventModel>>>
}
