package com.example.simbirsoftmobile.data.repositories.file

import com.example.simbirsoftmobile.data.network.dtos.category.CategoryNetworkDeserializer
import com.example.simbirsoftmobile.data.utils.getRequestFlowList
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.DataResult
import com.example.simbirsoftmobile.domain.core.Either
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

    override fun getCategories(): Flow<Either<DataError, DataResult<List<CategoryModel>>>> =
        getRequestFlowList(
            "categories.json",
            gson,
            CategoryNetworkDeserializer.typeToken,
        )

    override  fun updateCategoriesSettings(vararg categories: CategoryModel): Flow<Either<DataError, Unit>> {
        TODO("Not yet implemented")
    }
}
