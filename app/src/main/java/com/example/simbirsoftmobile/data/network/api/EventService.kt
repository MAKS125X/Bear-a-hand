package com.example.simbirsoftmobile.data.network.api

import com.example.simbirsoftmobile.data.network.api.requests.EventByIdRequest
import com.example.simbirsoftmobile.data.network.api.requests.EventsByCategoriesRequest
import com.example.simbirsoftmobile.data.network.dtos.event.EventDto
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface EventService {
    @Headers("Content-Type:application/json")
    @GET("events")
    suspend fun getEvents(): Response<List<EventDto>>

    @Headers("Content-Type:application/json")
    @GET("event/{id}")
    suspend fun getEventById(
        @Path("id") eventId: String,
    ): Response<List<EventDto>>

    @Headers("Content-Type:application/json")
    @POST("events/item")
    suspend fun getEventByRequest(
        @Body request: EventByIdRequest,
    ): Response<EventDto>

    @Headers("Content-Type:application/json")
    @POST("events")
    suspend fun getEvents(
        @Body request: EventsByCategoriesRequest,
    ): Response<List<EventDto>>
}
