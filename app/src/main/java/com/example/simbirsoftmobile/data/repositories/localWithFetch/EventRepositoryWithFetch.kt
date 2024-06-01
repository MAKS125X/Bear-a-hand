package com.example.simbirsoftmobile.data.repositories.localWithFetch

import android.util.Log
import com.example.simbirsoftmobile.data.local.TransactionProvider
import com.example.simbirsoftmobile.data.local.daos.EventDao
import com.example.simbirsoftmobile.data.local.entities.toOrganization
import com.example.simbirsoftmobile.data.network.api.EventService
import com.example.simbirsoftmobile.data.network.api.requests.EventsByCategoriesRequest
import com.example.simbirsoftmobile.data.network.dtos.event.toEntity
import com.example.simbirsoftmobile.data.network.dtos.event.toPartialEntity
import com.example.simbirsoftmobile.data.utils.getLocalResources
import com.example.simbirsoftmobile.data.utils.getRequestFlowDto
import com.example.simbirsoftmobile.data.utils.mapToDomain
import com.example.simbirsoftmobile.data.utils.networkBoundResource
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.DataResult
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.models.event.EventModel
import com.example.simbirsoftmobile.domain.models.event.OrganizationModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.domain.utils.mapDataResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class EventRepositoryWithFetch @Inject constructor(
    private val eventService: EventService,
    private val dao: EventDao,
    private val transactionProvider: TransactionProvider,
) : EventRepository {
    private var shouldFetch = true

    override fun getEventsByCategory(vararg categoryIds: String): Flow<Either<DataError, DataResult<List<EventModel>>>> =
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
                Log.d("Database", "getEventsByCategory: $list")
                dao.insertOrUpdateEvent(list.map { it.toPartialEntity() })
                shouldFetch = false
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
                dao.insertOrUpdateEvent(it.map { it.toPartialEntity() })
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
