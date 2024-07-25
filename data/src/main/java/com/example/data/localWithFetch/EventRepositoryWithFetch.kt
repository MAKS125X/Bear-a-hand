package com.example.data.localWithFetch

import com.example.api.EventService
import com.example.api.requests.EventsByCategoriesRequest
import com.example.core.models.event.EventModel
import com.example.core.models.event.OrganizationModel
import com.example.core.repositories.EventRepository
import com.example.data.getLocalResources
import com.example.data.getRequestFlowDto
import com.example.data.networkBoundResource
import com.example.data.toEntity
import com.example.data.toPartialEntity
import com.example.local.daos.EventDao
import com.example.local.entities.toOrganization
import com.example.mapper.mapToDomain
import com.example.result.DataError
import com.example.result.DataResult
import com.example.result.Either
import com.example.result.mapDataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class EventRepositoryWithFetch @Inject constructor(
    private val eventService: EventService,
    private val dao: EventDao,
) : EventRepository {
    private var shouldFetch = true

    override fun getEventsByCategory(
        vararg categoryIds: String,
    ): Flow<Either<DataError, DataResult<List<EventModel>>>> =
        networkBoundResource(
            localQuery = {
                dao.getEvents(categoryIds.toList())
            },
            apiFetch = {
                getRequestFlowDto {
                    eventService.getEvents(EventsByCategoriesRequest(categoryIds.toList()))
                }
            },
            saveFetchResult = { list ->
                dao.insertOrUpdateEvent(list.map { it.toPartialEntity() })
                this.shouldFetch = false
            },
            shouldFetch = shouldFetch,
        ).mapDataResult {
            it.mapToDomain()
        }

    override fun getEventById(id: String): Flow<Either<DataError, DataResult<EventModel>>> =
        networkBoundResource(
            localQuery = {
                dao.observeEventWithUpdatingState(id)
            },
            apiFetch = {
                getRequestFlowDto {
                    eventService.getEventById(id)
                }
            },
            saveFetchResult = {
                dao.addEvents(it.map { it.toEntity(true) })
            },
            shouldFetch = shouldFetch,
        ).mapDataResult {
            it.mapToDomain()
        }

    override fun getAllEvents(): Flow<Either<DataError, DataResult<List<EventModel>>>> =
        networkBoundResource(
            localQuery = {
                dao.getEvents()
            },
            apiFetch = {
                getRequestFlowDto {
                    eventService.getEvents()
                }
            },
            saveFetchResult = {
                dao.insertOrUpdateEvent(it.map { event -> event.toPartialEntity() })
                shouldFetch = false
            },
            shouldFetch = shouldFetch,
        ).mapDataResult {
            it.mapToDomain()
        }

    override fun searchEventsByName(substring: String): Flow<Either<DataError, DataResult<List<EventModel>>>> =
        networkBoundResource(
            localQuery = {
                dao.searchEventsByName(substring)
            },
            apiFetch = {
                getRequestFlowDto {
                    eventService.getEvents()
                }
            },
            saveFetchResult = {
                dao.insertOrUpdateEvent(it.map { it.toPartialEntity() })
                shouldFetch = false
            },
            shouldFetch = shouldFetch,
        ).mapDataResult {
            it.mapToDomain()
        }

    override fun searchOrganizations(substring: String): Flow<Either<DataError, DataResult<List<OrganizationModel>>>> =
        networkBoundResource(
            localQuery = {
                dao.searchEventsByOrganization(substring)
            },
            apiFetch = {
                getRequestFlowDto {
                    eventService.getEvents()
                }
            },
            saveFetchResult = { list ->
                dao.insertOrUpdateEvent(list.map { it.toPartialEntity() })
                shouldFetch = false
            },
            shouldFetch = shouldFetch,
        ).mapDataResult { list ->
            list.map { it.toOrganization() }.distinctBy { it.name }
        }

    override fun observeUnreadEventsByCategories(vararg categoryIds: String): Flow<Either<DataError, DataResult<Int>>> =
        getLocalResources {
            dao.observeUnreadEventsCountByCategory(categoryIds.toList())
                .distinctUntilChanged()
        }
}
