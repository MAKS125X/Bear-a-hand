package com.example.simbirsoftmobile.data.network.repositories

import com.example.simbirsoftmobile.data.network.api.EventService
import com.example.simbirsoftmobile.data.network.api.requests.EventByIdRequest
import com.example.simbirsoftmobile.data.network.api.requests.EventsByCategoriesRequest
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.EventModel
import com.example.simbirsoftmobile.domain.repositories.EventRepository
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class EventRepositoryNetwork(
    private val eventService: EventService
) : EventRepository {

    override fun getAllEvents(): Observable<Either<NetworkError, List<EventModel>>> {
        return Observable.create { o ->
            eventService
                .getEvents()
                .subscribeOn(Schedulers.io())
                .timeout(30, TimeUnit.SECONDS) {
                    o.onNext(Either.Left(NetworkError.Timeout))
                }
                .subscribe(
                    {
                        o.onNext(Either.Right(it.map { event -> event.mapToDomain() }))
                    }, {
                        o.onNext(Either.Left(NetworkError.Api(it.message)))
                    }
                )
        }
    }

    override fun getEventsByCategory(vararg categoryIds: String): Observable<Either<NetworkError, List<EventModel>>> {
        return Observable.create { o ->
            eventService
                .getEvents(EventsByCategoriesRequest(categoryIds.toList()))
                .subscribeOn(Schedulers.io())
                .timeout(30, TimeUnit.SECONDS) {
                    o.onNext(Either.Left(NetworkError.Timeout))
                }
                .subscribe(
                    {
                        o.onNext(Either.Right(it.map { event -> event.mapToDomain() }))
                    }, {
                        o.onNext(Either.Left(NetworkError.Api(it.message)))
                    }
                )
        }
    }

    override fun getEventById(id: String): Observable<Either<NetworkError, EventModel>> {
        return Observable.create { o ->
            eventService
                .getEventByRequest(EventByIdRequest(id))
                .subscribeOn(Schedulers.io())
                .timeout(30, TimeUnit.SECONDS) {
                    o.onNext(Either.Left(NetworkError.Timeout))
                }
                .subscribe(
                    {
                        o.onNext(Either.Right(it.mapToDomain()))
                    }, {
                        o.onNext(Either.Left(NetworkError.Api(it.message)))
                    }
                )
        }
    }
}
