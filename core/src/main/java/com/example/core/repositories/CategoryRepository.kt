package com.example.core.repositories

import com.example.core.models.category.CategoryModel
import com.example.result.DataError
import com.example.result.DataResult
import com.example.result.Either

import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
    fun getCategories(): Flow<Either<DataError, DataResult<List<CategoryModel>>>>

    fun getCategories(shouldFetch: Boolean): Flow<Either<DataError, DataResult<List<CategoryModel>>>>

    fun updateCategoriesSettings(vararg categories: CategoryModel): Flow<Either<DataError, Unit>>
}
