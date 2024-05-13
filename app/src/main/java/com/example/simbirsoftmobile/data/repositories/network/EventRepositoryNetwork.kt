package com.example.simbirsoftmobile.data.repositories.network

import com.example.simbirsoftmobile.data.network.api.EventService
import com.example.simbirsoftmobile.data.network.api.requests.EventByIdRequest
import com.example.simbirsoftmobile.data.network.api.requests.EventsByCategoriesRequest
import com.example.simbirsoftmobile.data.utils.getRequestFlowItem
import com.example.simbirsoftmobile.data.utils.getRequestFlowList
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.DataResult
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.models.EventModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.domain.utils.mapDataResult
import kotlinx.coroutines.flow.Flow

class EventRepositoryNetwork(
    private val eventService: EventService,
) : EventRepository {
    override fun getAllEvents(): Flow<Either<DataError, DataResult<List<EventModel>>>> =
        getRequestFlowList {
            eventService
                .getEvents()
        }

    override fun searchEventsByName(substring: String): Flow<Either<DataError, DataResult<List<EventModel>>>> =
        getRequestFlowList {
            eventService
                .getEvents()
        }.mapDataResult {
            it.filter { event ->
                event.name.contains(substring, true)
            }
        }

    override fun searchOrganizations(substring: String): Flow<Either<DataError, DataResult<List<EventModel>>>> =
        getRequestFlowList {
            eventService
                .getEvents()
        }.mapDataResult {
            it.filter { event ->
                event.organization.contains(substring, true)
            }
        }


    override fun getEventsByCategory(vararg categoryIds: String): Flow<Either<DataError, DataResult<List<EventModel>>>> =
        getRequestFlowList {
            eventService
                .getEvents(EventsByCategoriesRequest(categoryIds.toList()))
        }

    override fun getEventById(id: String): Flow<Either<DataError, DataResult<EventModel>>> =
        getRequestFlowItem {
            eventService
                .getEventByRequest(EventByIdRequest(id))
        }
}
