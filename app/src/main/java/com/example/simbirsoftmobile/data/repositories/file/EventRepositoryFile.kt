package com.example.simbirsoftmobile.data.repositories.file

import com.example.simbirsoftmobile.data.network.dtos.event.EventNetworkDeserializer
import com.example.simbirsoftmobile.data.network.dtos.event.EventsNetworkDeserializer
import com.example.simbirsoftmobile.data.utils.getRequestFlowList
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.DataResult
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.models.event.EventModel
import com.example.simbirsoftmobile.domain.models.event.OrganizationModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.example.simbirsoftmobile.domain.utils.mapDataResult
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow

class EventRepositoryFile : EventRepository {
    private val gson = GsonBuilder()
        .registerTypeAdapter(
            EventsNetworkDeserializer.typeToken,
            EventsNetworkDeserializer(),
        )
        .registerTypeAdapter(
            EventNetworkDeserializer.objectType,
            EventNetworkDeserializer(),
        )
        .create()

    private fun getAllEventsFromFile(): Flow<Either<DataError, DataResult<List<EventModel>>>> =
        getRequestFlowList(
            "events.json",
            gson,
            EventsNetworkDeserializer.typeToken,
        )

    override fun getEventsByCategory(vararg categoryIds: String): Flow<Either<DataError, DataResult<List<EventModel>>>> {
        return getAllEventsFromFile().mapDataResult { list ->
            list.filter { event -> event.category in categoryIds }
        }
    }

    override fun getEventById(id: String): Flow<Either<DataError, DataResult<EventModel>>> {
        return getAllEventsFromFile().mapDataResult { list ->
            list.first { event -> event.id == id }
        }
    }

    override fun getAllEvents(): Flow<Either<DataError, DataResult<List<EventModel>>>> =
        getAllEventsFromFile()

    override fun searchEventsByName(substring: String): Flow<Either<DataError, DataResult<List<EventModel>>>> =
        getAllEventsFromFile().mapDataResult {
            it.filter { event ->
                event.name.contains(substring, true)
            }
        }

    override fun searchOrganizations(substring: String): Flow<Either<DataError, DataResult<List<OrganizationModel>>>> =
        getAllEventsFromFile().mapDataResult { list ->
            list.filter { event ->
                event.organization.contains(substring, true)
            }.map {
                OrganizationModel(it.organization)
            }.distinctBy { it.name }
        }

    override fun observeUnreadEventsByCategories(vararg categoryIds: String): Flow<Either<DataError, DataResult<Int>>> {
        TODO("Not yet implemented")
    }
}
