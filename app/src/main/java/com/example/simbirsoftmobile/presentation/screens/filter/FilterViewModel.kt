package com.example.simbirsoftmobile.presentation.screens.filter

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.usecases.GetCategoriesUseCase
import com.example.simbirsoftmobile.domain.usecases.UpdateCategoriesSettingsUseCase
import com.example.simbirsoftmobile.domain.utils.processResult
import com.example.simbirsoftmobile.presentation.base.MviViewModel
import com.example.simbirsoftmobile.presentation.models.category.CategoryLongUi
import com.example.simbirsoftmobile.presentation.models.category.mapToUi
import com.example.simbirsoftmobile.presentation.models.utils.mapToDomain
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.plus
import javax.inject.Inject

class FilterViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
    private val updateCategoriesSettingsUseCase: UpdateCategoriesSettingsUseCase,
) : MviViewModel<FilterState, FilterSideEffect, FilterEvent>(
    FilterState()
) {
    init {
        consumeEvent(FilterEvent.Internal.LoadCategories)
    }

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

    private fun updateChanges(list: List<CategoryLongUi>) {
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

        getCategoriesUseCase
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
        private val getCategoriesUseCase: GetCategoriesUseCase,
        private val updateCategoriesSettingsUseCase: UpdateCategoriesSettingsUseCase,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == FilterViewModel::class.java)
            return FilterViewModel(getCategoriesUseCase, updateCategoriesSettingsUseCase) as T
        }
    }
}
