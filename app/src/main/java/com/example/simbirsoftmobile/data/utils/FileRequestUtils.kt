package com.example.simbirsoftmobile.data.utils

import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.DataResult
import com.example.simbirsoftmobile.domain.core.Either
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import java.lang.reflect.Type

private fun <T> getRequestFlow(
    fileName: String,
    gson: Gson,
    type: Type,
): Flow<Either<DataError, T>> = flow {
    emit(
        SimbirSoftApp.INSTANCE.assets
            .open(fileName)
            .bufferedReader()
            .use { it.readText() }
    )
}
    .onEach { delay(1000L) }
    .map {
        try {
            Either.Right(
                gson.fromJson<T>(
                    it,
                    type,
                )
            )
        } catch (e: Exception) {
            Either.Left(
                DataError.Api(e.localizedMessage)
            )
        }
    }
    .flowOn(Dispatchers.IO)

fun <Model, Dto : DataMapper<Model>> getRequestFlowItem(
    fileName: String,
    gson: Gson,
    type: Type,
): Flow<Either<DataError, DataResult<Model>>> = getRequestFlow<Dto>(fileName, gson, type).map {
    when (it) {
        is Either.Left -> it
        is Either.Right -> {
            Either.Right(
                DataResult.SuccessFromLocal(it.value.mapToDomain(), DataError.NetworkBlock)
            )
        }
    }
}

fun <Model, Dto : DataMapper<Model>> getRequestFlowList(
    fileName: String,
    gson: Gson,
    type: Type,
): Flow<Either<DataError, DataResult<List<Model>>>> =
    getRequestFlow<List<Dto>>(fileName, gson, type).map {
        when (it) {
            is Either.Left -> it
            is Either.Right -> {
                Either.Right(
                    DataResult.SuccessFromLocal(
                        it.value
                            .map { dtoItem -> dtoItem.mapToDomain() },
                        DataError.NetworkBlock
                    )
                )
            }
        }
    }
