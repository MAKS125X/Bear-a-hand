package com.example.result

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach

inline fun <T, S> Flow<Either<DataError, T>>.mapRightEither(
    crossinline transform: (T) -> S,
): Flow<Either<DataError, S>> =
    this.map { either ->
        when (either) {
            is Either.Left -> {
                either
            }

            is Either.Right -> {
                Either.Right(transform(either.value))
            }
        }
    }

inline fun <T, S> Flow<Either<DataError, DataResult<T>>>.mapDataResult(
    crossinline transform: (T) -> S,
): Flow<Either<DataError, DataResult<S>>> =
    this.mapRightEither {
        when (it) {
            is DataResult.SuccessFromApi -> {
                DataResult.SuccessFromApi(
                    transform(it.data)
                )
            }

            is DataResult.SuccessFromLocal -> {
                DataResult.SuccessFromLocal(
                    transform(it.data),
                    it.networkError,
                )
            }
        }
    }

fun <T> Flow<Either<DataError, DataResult<T>>>.extractResult(): Flow<Either<DataError, T>> =
    this.map { either ->
        when (either) {
            is Either.Left -> {
                either
            }

            is Either.Right -> {
                when (either.value) {
                    is DataResult.SuccessFromApi -> {
                        Either.Right(
                            either.value.data,
                        )
                    }

                    is DataResult.SuccessFromLocal -> {
                        Either.Right(
                            either.value.data,
                        )
                    }
                }
            }
        }
    }

fun <T> Either<DataError, T>.processResult(
    onError: (DataError) -> Unit,
    onSuccess: (T) -> Unit,
) {
    when (this) {
        is Either.Left -> onError(this.value)
        is Either.Right -> onSuccess(this.value)
    }
}

fun <R, L> Flow<Either<R, L>>.onEach(
    onLeft: () -> Unit,
    onRight: () -> Unit,
): Flow<Either<R, L>> =
    this.onEach { either ->
        when (either) {
            is Either.Left -> {
                onLeft()
            }

            is Either.Right -> {
                onRight()
            }
        }
    }
