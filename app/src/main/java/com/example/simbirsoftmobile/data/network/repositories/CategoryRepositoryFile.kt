package com.example.simbirsoftmobile.data.network.repositories

import com.example.simbirsoftmobile.data.network.dtos.category.CategoryNetworkDeserializer
import com.example.simbirsoftmobile.data.utils.getRequestFlowList
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.CategoryModel
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import com.google.gson.GsonBuilder
import kotlinx.coroutines.flow.Flow

class CategoryRepositoryFile : CategoryRepository {
    private val gson = GsonBuilder()
        .registerTypeAdapter(
            CategoryNetworkDeserializer.typeToken,
            CategoryNetworkDeserializer(),
        )
        .create()

    override fun getCategories(): Flow<Either<NetworkError, List<CategoryModel>>> =
        getRequestFlowList(
            "categories.json",
            gson,
            CategoryNetworkDeserializer.typeToken
        )
}

