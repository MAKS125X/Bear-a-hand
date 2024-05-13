package com.example.simbirsoftmobile.data.repositories.localWithFetch

import com.example.simbirsoftmobile.data.local.TransactionProvider
import com.example.simbirsoftmobile.data.local.daos.EventDao
import com.example.simbirsoftmobile.data.network.api.EventService
import com.example.simbirsoftmobile.data.network.api.requests.EventsByCategoriesRequest
import com.example.simbirsoftmobile.data.network.dtos.event.toEntity
import com.example.simbirsoftmobile.data.utils.getRequestFlowDto
import com.example.simbirsoftmobile.data.utils.mapToDomain
import com.example.simbirsoftmobile.data.utils.networkBoundResource
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.DataResult
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.models.EventModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.domain.utils.mapDataResult
import kotlinx.coroutines.flow.Flow

class EventRepositoryWithFetch(
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
            saveFetchResult = {
                transactionProvider.runAsTransaction {
                    dao.addEvents(it.map { it.toEntity() })
                }
                shouldFetch = false
            },
            shouldFetch = shouldFetch,
        ).mapDataResult {
            it.mapToDomain()
        }

    override fun getEventById(id: String): Flow<Either<DataError, DataResult<EventModel>>> =
        networkBoundResource(
            localQuery = {
                dao.getEventById(id)
            },
            apiFetch = {
                getRequestFlowDto {
                    eventService.getEventById(id)
                }
            },
            saveFetchResult = {
                transactionProvider.runAsTransaction {
                    dao.addEvents(it.map { it.toEntity() })
                }
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
                transactionProvider.runAsTransaction {
                    dao.addEvents(it.map { it.toEntity() })
                }
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
                transactionProvider.runAsTransaction {
                    dao.addEvents(it.map { it.toEntity() })
                }
                shouldFetch = false
            },
            shouldFetch = shouldFetch,
        ).mapDataResult {
            it.mapToDomain()
        }

    override fun searchOrganizations(substring: String): Flow<Either<DataError, DataResult<List<EventModel>>>> =
        networkBoundResource(
            localQuery = {
                dao.searchEventsByOrganization(substring)
            },
            apiFetch = {
                getRequestFlowDto {
                    eventService.getEvents()
                }
            },
            saveFetchResult = {
                transactionProvider.runAsTransaction {
                    dao.addEvents(it.map { it.toEntity() })
                }
                shouldFetch = false
            },
            shouldFetch = shouldFetch,
        ).mapDataResult {
            it.mapToDomain()
        }
}
