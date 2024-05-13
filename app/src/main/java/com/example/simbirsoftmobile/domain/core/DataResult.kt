package com.example.simbirsoftmobile.domain.core

sealed class DataResult<T> {
    class SuccessFromApi<T>(val data: T): DataResult<T>()
    class SuccessFromLocal<T>(val data: T, val networkError: DataError): DataResult<T>()
}
