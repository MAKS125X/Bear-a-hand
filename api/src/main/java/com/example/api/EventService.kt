package com.example.api

import com.example.api.dtos.event.EventDto
import com.example.api.requests.EventByIdRequest
import com.example.api.requests.EventsByCategoriesRequest
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

interface EventService {
    @GET("events")
    suspend fun getEvents(): Response<List<EventDto>>

    @GET("event/{id}")
    suspend fun getEventById(
        @Path("id") eventId: String,
    ): Response<List<EventDto>>

    @POST("events/item")
    suspend fun getEventByRequest(
        @Body request: EventByIdRequest,
    ): Response<EventDto>

    @POST("events")
    suspend fun getEvents(
        @Body request: EventsByCategoriesRequest,
    ): Response<List<EventDto>>
}
