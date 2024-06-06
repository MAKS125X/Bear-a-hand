package com.example.simbirsoftmobile.data.utils

import com.example.simbirsoftmobile.data.network.interceptors.networkMonitor.ConnectionException
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.DataResult
import com.example.simbirsoftmobile.domain.core.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withTimeoutOrNull
import retrofit2.Response

fun <Dto> getRequestFlowDto(
    apiCall: suspend () -> Response<Dto>,
): Flow<Either<DataError, Dto>> = flow {
    withTimeoutOrNull(5000L) {
        val response = apiCall.invoke()
        if (response.isSuccessful) {
            response.body()?.let {
                emit(Either.Right(it))
            }
        } else {
            response.errorBody()?.let { error ->
                error.close()
                emit(Either.Left(DataError.Api(error.string())))
            }
        }
    }
        ?: emit(Either.Left(DataError.Timeout))
}
    .catch {
        when (it) {
            is ConnectionException -> emit(Either.Left(DataError.Connection))
            else -> emit(Either.Left(DataError.Unexpected(it.localizedMessage)))
        }
    }
    .flowOn(Dispatchers.IO)

fun <Model, Dto : DataMapper<Model>> getRequestFlowItem(
    apiCall: suspend () -> Response<Dto>,
): Flow<Either<DataError, DataResult<Model>>> = getRequestFlowDto(apiCall).map {
    when (it) {
        is Either.Left -> it
        is Either.Right -> {
            Either.Right(DataResult.SuccessFromApi(it.value.mapToDomain()))
        }
    }
}

fun <Model, Dto : DataMapper<Model>> getRequestFlowList(
    apiCall: suspend () -> Response<List<Dto>>,
): Flow<Either<DataError, DataResult<List<Model>>>> = getRequestFlowDto(apiCall).map {
    when (it) {
        is Either.Left -> it
        is Either.Right -> {
            Either.Right(
                DataResult.SuccessFromApi(
                    it.value.map { dtoItem -> dtoItem.mapToDomain() }
                )
            )
        }
    }
}
