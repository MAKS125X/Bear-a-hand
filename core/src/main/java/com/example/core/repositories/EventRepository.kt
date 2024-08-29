package com.example.core.repositories

import com.example.core.models.event.EventModel
import com.example.core.models.event.OrganizationModel
import com.example.result.DataError
import com.example.result.DataResult
import com.example.result.Either
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEventsByCategory(vararg categoryIds: String): Flow<Either<DataError, DataResult<List<EventModel>>>>

    fun getEventById(id: String): Flow<Either<DataError, DataResult<EventModel>>>

    fun getAllEvents(): Flow<Either<DataError, DataResult<List<EventModel>>>>

    fun searchEventsByName(substring: String): Flow<Either<DataError, DataResult<List<EventModel>>>>

    fun searchOrganizations(substring: String): Flow<Either<DataError, DataResult<List<OrganizationModel>>>>

    fun observeUnreadEventsByCategories(vararg categoryIds: String): Flow<Either<DataError, DataResult<Int>>>
}
