package com.example.category_settings.usecase

import com.example.core.models.category.CategoryModel
import com.example.core.repositories.CategoryRepository
import javax.inject.Inject

class UpdateCategoriesSettingsUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke(vararg models: CategoryModel) =
        categoryRepository.updateCategoriesSettings(*models)
}
