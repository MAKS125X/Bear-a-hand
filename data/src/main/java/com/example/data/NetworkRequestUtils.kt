package com.example.data

import com.example.mapper.DataMapper
import com.example.network.networkMonitor.ConnectionException
import com.example.result.DataError
import com.example.result.DataResult
import com.example.result.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import retrofit2.Response
import java.net.SocketTimeoutException

fun <Dto> getRequestFlowDto(
    apiCall: suspend () -> Response<Dto>,
): Flow<Either<DataError, Dto>> = flow {
    val response = apiCall.invoke()
    emit(
        if (response.isSuccessful) {
            response.body()?.let {
                Either.Right(it)
            }
        } else {
            response.errorBody()?.let { error ->
                error.close()
                Either.Left(DataError.Api(error.string()))
            }
        }
            ?: Either.Left(DataError.Unexpected())
    )
}
    .catch {
        when (it) {
            is ConnectionException -> emit(Either.Left(DataError.Connection))
            is SocketTimeoutException -> emit(Either.Left(DataError.Timeout))
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
