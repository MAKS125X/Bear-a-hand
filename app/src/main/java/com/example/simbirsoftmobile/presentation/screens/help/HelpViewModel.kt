package com.example.simbirsoftmobile.presentation.screens.help

import androidx.lifecycle.viewModelScope
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.usecases.GetCategoriesUseCase
import com.example.simbirsoftmobile.domain.utils.processResult
import com.example.simbirsoftmobile.presentation.base.MviViewModel
import com.example.simbirsoftmobile.presentation.models.category.mapToUi
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus

class HelpViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase = GetCategoriesUseCase()
) : MviViewModel<HelpState, HelpSideEffect, HelpEvent>(
    HelpState()
) {
    init {
        consumeEvent(HelpEvent.Internal.LoadCategories)
    }

    override fun reduce(state: HelpState, event: HelpEvent) {
        when (event) {
            HelpEvent.Internal.LoadCategories -> {
                updateState(
                    state.copy(
                        isLoading = true,
                        error = null,
                    )
                )
                loadCategories()
            }

            is HelpEvent.Internal.CategoriesLoaded -> {
                updateState(
                    state.copy(
                        isLoading = false,
                        error = null,
                        categories = event.categories,
                    )
                )
            }

            is HelpEvent.Internal.ErrorLoaded -> {
                updateState(
                    state.copy(
                        isLoading = false,
                        error = event.error,
                    )
                )
            }
        }
    }

    private fun loadCategories() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            consumeEvent(HelpEvent.Internal.ErrorLoaded(DataError.Unexpected()))
        }

        getCategoriesUseCase
            .invoke()
            .onEach { response ->
                response.processResult(
                    onError = {
                        consumeEvent(HelpEvent.Internal.ErrorLoaded(it))
                    },
                    onSuccess = { list ->
                        consumeEvent(
                            HelpEvent.Internal.CategoriesLoaded(
                                list.map { it.mapToUi() }
                            )
                        )

                    }
                )
            }
            .launchIn(viewModelScope + exceptionHandler)
    }
}
