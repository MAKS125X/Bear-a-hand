package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.domain.models.CategoryModel
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import javax.inject.Inject

class UpdateCategoriesSettingsUseCase @Inject constructor(
    private val categoryRepository: CategoryRepository,
) {
    operator fun invoke(vararg models: CategoryModel) =
        categoryRepository.updateCategoriesSettings(*models)
}
