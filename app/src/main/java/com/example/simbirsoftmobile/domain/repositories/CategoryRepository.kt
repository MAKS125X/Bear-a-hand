package com.example.simbirsoftmobile.domain.repositories

import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.DataResult
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.models.CategoryModel
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<Either<DataError, DataResult<List<CategoryModel>>>>

    fun getCategories(shouldFetch: Boolean): Flow<Either<DataError, DataResult<List<CategoryModel>>>>

    fun updateCategoriesSettings(vararg categories: CategoryModel): Flow<Either<DataError, Unit>>
}
