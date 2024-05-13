package com.example.simbirsoftmobile.domain.repositories

import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.DataResult
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.models.EventModel
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEventsByCategory(vararg categoryIds: String): Flow<Either<DataError, DataResult<List<EventModel>>>>

    fun getEventById(id: String): Flow<Either<DataError, DataResult<EventModel>>>

    fun getAllEvents(): Flow<Either<DataError, DataResult<List<EventModel>>>>

    fun searchEventsByName(substring: String): Flow<Either<DataError, DataResult<List<EventModel>>>>

    fun searchOrganizations(substring: String): Flow<Either<DataError, DataResult<List<EventModel>>>>
}
