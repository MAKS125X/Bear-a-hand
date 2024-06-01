package com.example.simbirsoftmobile.domain.repositories

import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.DataResult
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.models.event.EventModel
import com.example.simbirsoftmobile.domain.models.event.OrganizationModel
import kotlinx.coroutines.flow.Flow

interface EventRepository {
    fun getEventsByCategory(vararg categoryIds: String): Flow<Either<DataError, DataResult<List<EventModel>>>>

    fun getEventById(id: String): Flow<Either<DataError, DataResult<EventModel>>>

    fun getAllEvents(): Flow<Either<DataError, DataResult<List<EventModel>>>>

    fun searchEventsByName(substring: String): Flow<Either<DataError, DataResult<List<EventModel>>>>

    fun searchOrganizations(substring: String): Flow<Either<DataError, DataResult<List<OrganizationModel>>>>

    fun observeUnreadEventsByCategories(vararg categoryIds: String): Flow<Either<DataError, DataResult<Int>>>
}
