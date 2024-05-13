package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import com.example.simbirsoftmobile.domain.utils.extractResult

class GetCategoriesUseCase(
    private val repository: CategoryRepository = SimbirSoftApp.INSTANCE.appContainer.categoryRepository,
) {
    operator fun invoke() = repository.getCategories().extractResult()
}
