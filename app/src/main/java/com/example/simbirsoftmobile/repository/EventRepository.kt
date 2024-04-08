package com.example.simbirsoftmobile.repository

import android.content.Context
import com.example.simbirsoftmobile.presentation.models.event.Event
import com.example.simbirsoftmobile.presentation.models.event.EventsDeserializer
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.rx3.asFlow
import java.util.concurrent.TimeUnit

object EventRepository {
    private const val TAG = "EventRepository"

    private val readEventIds: MutableSet<Int> = mutableSetOf()

    private val _notificationFlow: MutableSharedFlow<Int> = MutableSharedFlow(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST,
    )
    val notificationFlow: SharedFlow<Int> = _notificationFlow.asSharedFlow()

    private var currentEventList: MutableList<Event> = mutableListOf()

    private val eventGson by lazy {
        GsonBuilder().registerTypeAdapter(
            EventsDeserializer.objectType,
            EventsDeserializer(),
        ).create()
    }

    private fun emitUnreadValue() {
        val unreadCount = currentEventList.count { !readEventIds.contains(it.id) }
        _notificationFlow.tryEmit(unreadCount)
    }

    private fun getAllEvents(context: Context): Observable<List<Event>> {
        return Observable
            .just(context.assets.open("events.json").bufferedReader().use { it.readText() })
            .subscribeOn(Schedulers.io())
            .delay(2000, TimeUnit.MILLISECONDS)
            .map { eventGson.fromJson(it, EventsDeserializer.objectType) }
    }

    fun getAllEventsByCategories(
        requiredCategories: List<Int>,
        context: Context,
    ): Observable<List<Event>> =
        getAllEvents(context)
            .map { list ->
                list.filter { event ->
                    event.categoryList.any { requiredCategories.contains(it) }
                }
            }.doOnNext {
                currentEventList = it.toMutableList()
                emitUnreadValue()
            }

    fun searchEvent(
        searchString: String,
        context: Context,
    ): Flow<List<Event>> =
        getAllEvents(context).asFlow()
            .map {
                it.filter { event -> event.title.contains(searchString, true) }
            }

    fun getEventById(
        id: Int,
        context: Context,
    ): Single<Event> =
        getAllEvents(context)
            .map { list -> list.first { it.id == id } }
            .doOnNext {
                readEventIds.add(id)
                emitUnreadValue()
            }
            .singleOrError()
}
