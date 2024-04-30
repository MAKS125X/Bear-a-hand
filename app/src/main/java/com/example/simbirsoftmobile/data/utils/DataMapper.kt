package com.example.simbirsoftmobile.data.utils

fun interface DataMapper<T> {
    fun mapToDomain(): T
}
