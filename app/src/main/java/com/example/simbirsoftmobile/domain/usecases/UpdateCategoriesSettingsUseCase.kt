package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.models.CategoryModel
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository

class UpdateCategoriesSettingsUseCase(
    private val categoryRepository: CategoryRepository = SimbirSoftApp.INSTANCE.appContainer.categoryRepository
) {
    operator fun invoke(vararg models: CategoryModel) =
        categoryRepository.updateCategoriesSettings(*models)
}
