package com.example.simbirsoftmobile.domain.repositories

import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.CategoryModel
import kotlinx.coroutines.flow.Flow

interface CategoryRepository {
      fun getCategories(): Flow<Either<NetworkError, List<CategoryModel>>>
}
