package com.example.simbirsoftmobile.repository

import android.content.Context
import com.example.simbirsoftmobile.presentation.models.event.Event
import com.example.simbirsoftmobile.presentation.models.event.EventsDeserializer
import com.google.gson.GsonBuilder

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

    fun getEventById(
        id: Int,
        context: Context,
    ): Event {
        Thread.sleep(2_000)
        val events = getAllEvents(context)

        return events.first { it.id == id }
    }
}
