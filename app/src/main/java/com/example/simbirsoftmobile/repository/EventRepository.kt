package com.example.simbirsoftmobile.repository

import android.content.Context
import com.example.simbirsoftmobile.presentation.models.event.Event
import com.example.simbirsoftmobile.presentation.models.event.EventsDeserializer
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.subjects.BehaviorSubject

object EventRepository {
    private val readEventIds: MutableSet<Int> = mutableSetOf()
    val subject = BehaviorSubject.create<Int>()
    private var currentEventList: MutableList<Event> = mutableListOf()

    private val eventGson by lazy {
        GsonBuilder().registerTypeAdapter(
            EventsDeserializer.objectType,
            EventsDeserializer(),
        ).create()
    }

    private fun getAllEvents(context: Context): List<Event> {
        val json =
            context.assets
                .open("events.json")
                .bufferedReader()
                .use {
                    it.readText()
                }

        return eventGson.fromJson(json, EventsDeserializer.objectType)
    }

    fun getAllEventsByCategories(
        requiredCategories: List<Int>,
        context: Context,
    ): List<Event> {
        val events = getAllEvents(context)

        val filteredEvents = events.filter { event ->
            event.categoryList.any { requiredCategories.contains(it) }
        }

        currentEventList = filteredEvents.toMutableList()

        emitUnreadValue()

        return filteredEvents
    }

    fun searchEvent(
        searchString: String,
        context: Context,
    ): Single<List<Event>> {
        val events = getAllEvents(context)

        return Single.just(events.filter { event ->
            event.title.contains(searchString, true)
        })
    }

    private fun emitUnreadValue() {
        val unreadCount = currentEventList.count { !readEventIds.contains(it.id) }
        subject.onNext(unreadCount)
    }

    fun getEventById(
        id: Int,
        context: Context,
    ): Event {
        Thread.sleep(1_000)
        val events = getAllEvents(context)
        readEventIds.add(id)
        emitUnreadValue()

        return events.first { it.id == id }
    }
}
