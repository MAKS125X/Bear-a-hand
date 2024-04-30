package com.example.simbirsoftmobile.data.network.repositories

import com.example.simbirsoftmobile.data.network.dtos.event.EventDto
import com.example.simbirsoftmobile.data.network.dtos.event.EventNetworkDeserializer
import com.example.simbirsoftmobile.data.network.dtos.event.EventsNetworkDeserializer
import com.example.simbirsoftmobile.data.network.dtos.event.toModel
import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.EventModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class EventRepositoryFile : EventRepository {
    private val gson = GsonBuilder()
        .registerTypeAdapter(
            EventsNetworkDeserializer.typeToken,
            EventsNetworkDeserializer()
        )
        .registerTypeAdapter(
            EventNetworkDeserializer.objectType,
            EventNetworkDeserializer()
        )
        .create()

    private fun getAllEventsFromFile(): Observable<Either<NetworkError, List<EventModel>>> {
        return Observable
            .just(
                SimbirSoftApp.INSTANCE.assets
                    .open("events.json")
                    .bufferedReader()
                    .use { it.readText() }
            )
            .subscribeOn(Schedulers.io())
            .delay(1, TimeUnit.SECONDS)
            .map {
                try {
                    Either.Right(
                        gson.fromJson<List<EventDto>>(
                            it,
                            EventsNetworkDeserializer.typeToken
                        ).map { event-> event.toModel() }
                    )
                } catch (e: Exception) {
                    Either.Left(
                        NetworkError.Api(e.localizedMessage)
                    )
                }
            }
    }

    override fun getEventsByCategory(vararg categoryIds: String): Observable<Either<NetworkError, List<EventModel>>> {
        return getAllEventsFromFile().map { response ->
            when (response) {
                is Either.Left -> response
                is Either.Right -> {
                    Either.Right(response.value.filter { event -> event.category in categoryIds })
                }
            }
        }
    }

    override fun getEventById(id: String): Observable<Either<NetworkError, EventModel>> {
        return getAllEventsFromFile().map { response ->
            when (response) {
                is Either.Left -> response
                is Either.Right -> {
                    Either.Right(response.value.first { event -> event.id == id })
                }
            }
        }
    }

    override fun getAllEvents(): Observable<Either<NetworkError, List<EventModel>>> =
        getAllEventsFromFile()
}
