package com.example.simbirsoftmobile.data.network.repositories

import com.example.simbirsoftmobile.data.network.dtos.event.EventNetworkDeserializer
import com.example.simbirsoftmobile.data.network.dtos.event.EventsNetworkDeserializer
import com.example.simbirsoftmobile.data.utils.getRequestFlowList
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.EventModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

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

    private fun getAllEventsFromFile(): Flow<Either<NetworkError, List<EventModel>>> =
        getRequestFlowList(
            "events.json",
            gson,
            EventsNetworkDeserializer.typeToken,
        )

    override fun getEventsByCategory(vararg categoryIds: String): Flow<Either<NetworkError, List<EventModel>>> {
        return getAllEventsFromFile().map { response ->
            when (response) {
                is Either.Left -> response
                is Either.Right -> {
                    Either.Right(response.value.filter { event -> event.category in categoryIds })
                }
            }
        }
    }

    override fun getEventById(id: String): Flow<Either<NetworkError, EventModel>> {
        return getAllEventsFromFile().map { response ->
            when (response) {
                is Either.Left -> response
                is Either.Right -> {
                    Either.Right(response.value.first { event -> event.id == id })
                }
            }
        }
    }

    override fun getAllEvents(): Flow<Either<NetworkError, List<EventModel>>> =
        getAllEventsFromFile()
}
