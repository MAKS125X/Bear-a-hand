package com.example.simbirsoftmobile.domain.usecases

import com.example.simbirsoftmobile.di.SimbirSoftApp
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.core.NetworkError
import com.example.simbirsoftmobile.domain.models.CategorySettingModel
import com.example.simbirsoftmobile.domain.models.toSettingModel
import com.example.simbirsoftmobile.domain.repositories.CategoryRepository
import com.example.simbirsoftmobile.presentation.models.settingTest.CategorySettingPrefs
import io.reactivex.rxjava3.core.Observable

class GetCategoriesWithSettingsUseCase(
    private val repository: CategoryRepository =  SimbirSoftApp.INSTANCE.appContainer.categoryRepository
) {
    operator fun invoke(settings: List<CategorySettingPrefs>): Observable<Either<NetworkError, List<CategorySettingModel>>> {
        return repository.getCategories().map { response ->
            when (response) {
                is Either.Left -> {
                    response
                }

                is Either.Right -> {
                    val categorySetting =
                        response.value.map { category ->
                            category.toSettingModel(
                                settings
                                    .find { it.id == category.id }?.isSelected ?: false
                            )
                        }

                    Either.Right(categorySetting)
                }
            }
        }
    }
}
