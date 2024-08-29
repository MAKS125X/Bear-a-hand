package com.example.help.usecase

import com.example.core.repositories.CategoryRepository
import com.example.result.extractResult
import javax.inject.Inject

class GetCategoriesUseCase @Inject constructor(
    private val repository: CategoryRepository,
) {
    operator fun invoke() = repository.getCategories().extractResult()
}
