package com.example.simbirsoftmobile.data.repositories.network

import com.example.simbirsoftmobile.data.network.api.CategoryService
import com.example.simbirsoftmobile.data.utils.getRequestFlowList
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.DataResult
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.models.CategoryModel
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import kotlinx.coroutines.flow.Flow

class CategoryRepositoryNetwork(
    private val categoryService: CategoryService,
) : CategoryRepository {
    override fun getCategories(): Flow<Either<DataError, DataResult<List<CategoryModel>>>> =
        getRequestFlowList {
            categoryService
                .getCategories()
        }

    override fun updateCategoriesSettings(vararg categories: CategoryModel): Flow<Either<DataError, Unit>> {
        TODO("Not yet implemented")
    }
}
