package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import com.example.simbirsoftmobile.domain.utils.extractResult
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository,
) {
    operator fun invoke() = repository.getCategories().extractResult()
}
