package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.CategorySettingModel
import com.example.simbirsoftmobile.domain.models.toSettingModel
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import com.example.simbirsoftmobile.presentation.models.settingTest.CategorySettingPrefs
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetCategoriesWithSettingsUseCase(
    private val repository: CategoryRepository = SimbirSoftApp.INSTANCE.appContainer.categoryRepository,
) {
    operator fun invoke(settings: List<CategorySettingPrefs>): Flow<Either<NetworkError, List<CategorySettingModel>>> =
        repository.getCategories().map { response ->
            when (response) {
                is Either.Left -> {
                    response
                }

                is Either.Right -> {
                    val categorySetting =
                        response.value.map { category ->
                            category.toSettingModel(
                                settings.find { it.id == category.id }?.isSelected ?: false
                            )
                        }

                    Either.Right(categorySetting)
                }
            }
        }
}
