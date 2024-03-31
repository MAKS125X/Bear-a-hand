package com.example.simbirsoftmobile.repository

import android.content.Context
import com.example.simbirsoftmobile.presentation.models.event.Event
import com.example.simbirsoftmobile.presentation.models.event.EventsDeserializer
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.core.Single

object EventRepository {
    private val eventGson by lazy {
        GsonBuilder().registerTypeAdapter(
            EventsDeserializer.objectType,
            EventsDeserializer(),
        ).create()
    }

    fun getAllEvents(context: Context): List<Event> {
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

        return events.filter { event ->
            event.categoryList.any { requiredCategories.contains(it) }
        }
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

    fun getEventById(
        id: Int,
        context: Context,
    ): Event {
        Thread.sleep(2_000)
        val events = getAllEvents(context)

        return events.first { it.id == id }
    }
}
