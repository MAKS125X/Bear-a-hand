package com.example.mapper

fun interface DataMapper<T> {
    fun mapToDomain(): T
}

fun <Model> List<DataMapper<Model>>.mapToDomain(): List<Model> {
    return this.map {
        it.mapToDomain()
    }
}
