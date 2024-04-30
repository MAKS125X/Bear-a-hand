package com.example.simbirsoftmobile.data.utils

import com.example.simbirsoftmobile.data.network.interceptors.networkMonitor.ConnectionException
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response

private fun <Dto> getRequestFlowDto(
    apiCall: suspend () -> Response<Dto>
): Flow<Either<NetworkError, Dto>> = flow {
    withTimeoutOrNull(30000L) {
        try {
            val response = apiCall.invoke()
            if (response.isSuccessful) {
                response.body()?.let {
                    emit(Either.Right(it))
                }
            } else {
                response.errorBody()?.let { error ->
                    error.close()
                    emit(Either.Left(NetworkError.Api(error.string())))
                }
            }
        } catch (e: ConnectionException) {
            emit(Either.Left(NetworkError.Connection))
        } catch (e: Exception) {
            emit(Either.Left(NetworkError.Unexpected(e.localizedMessage)))
        }
    } ?: emit(Either.Left(NetworkError.Timeout))
}.flowOn(Dispatchers.IO)

fun <Model, Dto : DataMapper<Model>> getRequestFlowItem(
    apiCall: suspend () -> Response<Dto>
): Flow<Either<NetworkError, Model>> = getRequestFlowDto(apiCall).map {
    when (it) {
        is Either.Left -> it
        is Either.Right -> {
            Either.Right(it.value.mapToDomain())
        }
    }
}

fun <Model, Dto : DataMapper<Model>> getRequestFlowList(
    apiCall: suspend () -> Response<List<Dto>>
): Flow<Either<NetworkError, List<Model>>> = getRequestFlowDto(apiCall).map {
    when (it) {
        is Either.Left -> it
        is Either.Right -> {
            Either.Right(
                it.value
                    .map { dtoItem -> dtoItem.mapToDomain() }
            )
        }
    }
}
