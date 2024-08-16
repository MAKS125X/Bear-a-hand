package com.example.category_settings.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.category_settings.model.SettingUi
import com.example.category_settings.model.mapToUi
import com.example.category_settings.usecase.GetCategorySettingsUseCase
import com.example.category_settings.usecase.UpdateCategoriesSettingsUseCase
import com.example.mapper.mapToDomain
import com.example.result.DataError
import com.example.result.processResult
import com.example.ui.MviViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject
import javax.inject.Provider

class FilterViewModel(
    private val getCategorySettingsUseCase: GetCategorySettingsUseCase,
    private val updateCategoriesSettingsUseCase: UpdateCategoriesSettingsUseCase,
) : MviViewModel<FilterState, FilterSideEffect, FilterEvent>(
    FilterState()
) {
    override fun reduce(state: FilterState, event: FilterEvent) {
        when (event) {
            is FilterEvent.Ui.UpdateSelectedById -> {
                for (category in state.categories) {
                    if (category.id == event.id) {
                        category.isSelected = !category.isSelected
                        break
                    }
                }

                updateState(
                    state.copy(
                        categories = state.categories
                    )
                )
            }

            is FilterEvent.Internal.CategoriesLoaded -> {
                updateState(
                    state.copy(
                        isLoading = false,
                        error = null,
                        categories = event.categories
                    )
                )
            }

            is FilterEvent.Internal.ErrorLoaded -> {
                updateState(
                    state.copy(
                        isLoading = false,
                        error = event.error,
                        categories = emptyList(),
                    )
                )
            }

            FilterEvent.Internal.LoadCategories -> {
                updateState(
                    state.copy(
                        isLoading = true,
                        error = null,
                        categories = emptyList()
                    )
                )
                loadCategories()
            }

            FilterEvent.Ui.AcceptChanges -> {
                updateChanges(state.categories)
            }
        }
    }

    private fun updateChanges(list: List<SettingUi>) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            processSideEffect(FilterSideEffect.ErrorChangesUpdate(DataError.Unexpected()))
        }

        viewModelScope.launch(exceptionHandler) {
            updateCategoriesSettingsUseCase
                .invoke(*list.mapToDomain().toTypedArray())
                .collect { either ->
                    either.processResult(
                        onError = {
                            processSideEffect(FilterSideEffect.ErrorChangesUpdate(it))
                        },
                        onSuccess = {
                            processSideEffect(FilterSideEffect.SuccessChangesUpdate)
                        }
                    )
                }
        }
    }

    private fun loadCategories() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            consumeEvent(FilterEvent.Internal.ErrorLoaded(DataError.Unexpected()))
        }

        getCategorySettingsUseCase
            .invoke()
            .onEach { either ->
                either.processResult(
                    onError = {
                        consumeEvent(FilterEvent.Internal.ErrorLoaded(it))
                    },
                    onSuccess = { list ->
                        consumeEvent(
                            FilterEvent.Internal.CategoriesLoaded(
                                list.map { it.mapToUi() }
                            )
                        )
                    }
                )
            }
            .launchIn(viewModelScope + exceptionHandler)
    }

    class Factory @Inject constructor(
        private val getCategorySettingsUseCase: Provider<GetCategorySettingsUseCase>,
        private val updateCategoriesSettingsUseCase: Provider<UpdateCategoriesSettingsUseCase>,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == FilterViewModel::class.java)
            return FilterViewModel(
                getCategorySettingsUseCase.get(),
                updateCategoriesSettingsUseCase.get()
            ) as T
        }
    }
}
