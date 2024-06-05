package com.example.data

import com.example.result.DataError
import com.example.result.DataResult
import com.example.result.Either
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.emitAll
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map

@OptIn(ExperimentalCoroutinesApi::class)
inline fun <Dto, Entity> networkBoundResource(
    crossinline localQuery: () -> Flow<Entity>,
    crossinline apiFetch: suspend () -> Flow<Either<DataError, Dto>>,
    crossinline saveFetchResult: suspend (Dto) -> Unit,
    shouldFetch: Boolean,
): Flow<Either<DataError, DataResult<Entity>>> {
    return flow {
        val flow: Flow<Either<DataError, DataResult<Entity>>> = if (shouldFetch) {
            val apiResponse = apiFetch()

            apiResponse
                .flatMapLatest { response ->
                    when (response) {
                        is Either.Left -> {
                            localQuery().map {
                                Either.Right(
                                    DataResult.SuccessFromLocal(
                                        it,
                                        response.value
                                    )
                                )
                            }

                        }

                        is Either.Right -> {
                            saveFetchResult(response.value)
                            localQuery().map {
                                Either.Right(DataResult.SuccessFromApi(it))
                            }
                        }
                    }
                }
        } else {
            getLocalResources(localQuery)
        }

        emitAll(flow)
    }.catch {
        emit(Either.Left(DataError.Unexpected(it.localizedMessage)))
    }
        .flowOn(Dispatchers.IO)
}

inline fun <Entity> getLocalResources(
    crossinline localQuery: () -> Flow<Entity>,
): Flow<Either<DataError, DataResult<Entity>>> {
    return localQuery().map {
        Either.Right(
            DataResult.SuccessFromLocal(
                it,
                DataError.NetworkBlock
            )
        )
    }
}
