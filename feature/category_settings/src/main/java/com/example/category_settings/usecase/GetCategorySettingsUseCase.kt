package com.example.category_settings.usecase

import com.example.core.repositories.CategoryRepository
import com.example.result.extractResult
import javax.inject.Inject

class GetCategorySettingsUseCase @Inject constructor(
    private val repository: CategoryRepository,
) {
    operator fun invoke() = repository.getCategories().extractResult()
}
