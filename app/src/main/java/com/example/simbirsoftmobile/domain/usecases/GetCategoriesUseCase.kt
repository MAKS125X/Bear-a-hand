package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository

class GetCategoriesUseCase(
    private val repository: CategoryRepository = SimbirSoftApp.INSTANCE.appContainer.categoryRepository,
) {
    operator fun invoke() = repository.getCategories()
}
