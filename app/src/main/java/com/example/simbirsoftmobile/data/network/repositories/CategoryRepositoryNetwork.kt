package com.example.simbirsoftmobile.data.network.repositories

import com.example.simbirsoftmobile.data.network.api.CategoryService
import com.example.simbirsoftmobile.data.utils.getRequestFlowList
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.CategoryModel
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import kotlinx.coroutines.flow.Flow

class CategoryRepositoryNetwork(
    private val categoryService: CategoryService,
) : CategoryRepository {
    override fun getCategories(): Flow<Either<NetworkError, List<CategoryModel>>> =
        getRequestFlowList {
            categoryService
                .getCategories()
        }
}
