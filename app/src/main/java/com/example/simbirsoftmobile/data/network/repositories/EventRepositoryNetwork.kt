package com.example.simbirsoftmobile.data.network.repositories

import com.example.simbirsoftmobile.data.network.api.EventService
import com.example.simbirsoftmobile.data.network.api.requests.EventByIdRequest
import com.example.simbirsoftmobile.data.network.api.requests.EventsByCategoriesRequest
import com.example.simbirsoftmobile.data.utils.getRequestFlowItem
import com.example.simbirsoftmobile.data.utils.getRequestFlowList
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.EventModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import kotlinx.coroutines.flow.Flow

class EventRepositoryNetwork(
    private val eventService: EventService,
) : EventRepository {
    override fun getAllEvents(): Flow<Either<NetworkError, List<EventModel>>> =
        getRequestFlowList {
            eventService
                .getEvents()
        }


    override fun getEventsByCategory(vararg categoryIds: String): Flow<Either<NetworkError, List<EventModel>>> =
        getRequestFlowList {
            eventService
                .getEvents(EventsByCategoriesRequest(categoryIds.toList()))
        }

    override fun getEventById(id: String): Flow<Either<NetworkError, EventModel>> =
        getRequestFlowItem {
            eventService
                .getEventByRequest(EventByIdRequest(id))
        }
}
