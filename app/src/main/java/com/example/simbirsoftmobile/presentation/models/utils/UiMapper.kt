package com.example.simbirsoftmobile.presentation.models.utils

fun interface UiMapper<Model> {
    fun mapToDomain(): Model
}

fun <Model> List<UiMapper<Model>>.mapToDomain(): List<Model> {
    return this.map {
        it.mapToDomain()
    }
}
