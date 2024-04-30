package com.example.simbirsoftmobile.data.network.repositories

import com.example.simbirsoftmobile.data.network.dtos.category.CategoryDto
import com.example.simbirsoftmobile.data.network.dtos.category.CategoryNetworkDeserializer
import com.example.simbirsoftmobile.data.network.dtos.category.toModel
import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.CategoryModel
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import com.google.gson.GsonBuilder
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import java.util.concurrent.TimeUnit

class CategoryRepositoryFile : CategoryRepository {
    private val gson = GsonBuilder()
        .registerTypeAdapter(
            CategoryNetworkDeserializer.typeToken,
            CategoryNetworkDeserializer()
        )
        .create()

    override fun getCategories(): Observable<Either<NetworkError, List<CategoryModel>>> {
        return Observable
            .just(
                SimbirSoftApp.INSTANCE.assets
                    .open("categories.json")
                    .bufferedReader()
                    .use { it.readText() }
            )
            .subscribeOn(Schedulers.io())
            .delay(1, TimeUnit.SECONDS)
            .map {
                try {
                    Either.Right(
                        gson.fromJson<List<CategoryDto>>(
                            it,
                            CategoryNetworkDeserializer.typeToken
                        ).map { category -> category.toModel() }
                    )
                } catch (e: Exception) {
                    Either.Left(
                        NetworkError.Api(e.localizedMessage)
                    )
                }
            }
    }
}
