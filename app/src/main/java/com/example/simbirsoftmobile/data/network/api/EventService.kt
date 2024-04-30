package com.example.simbirsoftmobile.data.network.api

import com.example.simbirsoftmobile.data.network.api.requests.EventByIdRequest
import com.example.simbirsoftmobile.data.network.api.requests.EventsByCategoriesRequest
import com.example.simbirsoftmobile.data.network.dtos.event.EventDto
import io.reactivex.rxjava3.core.Observable
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path

interface EventService {
    @Headers("Content-Type:application/json")
    @GET("events")
    fun getEvents(): Observable<List<EventDto>>

    @Headers("Content-Type:application/json")
    @GET("event/{id}")
    fun getEventById(
        @Path("id") eventId: String,
    ): Observable<List<EventDto>>

    @Headers("Content-Type:application/json")
    @POST("events/item")
    fun getEventByRequest(
        @Body request: EventByIdRequest,
    ): Observable<EventDto>

    @Headers("Content-Type:application/json")
    @POST("events")
    fun getEvents(
        @Body request: EventsByCategoriesRequest,
    ): Observable<List<EventDto>>
}
