package com.example.help.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.help.model.mapToUi
import com.example.help.usecase.GetCategoriesUseCase
import com.example.result.DataError
import com.example.result.processResult
import com.example.ui.MviViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import javax.inject.Inject

class HelpViewModel(
    private val getCategoriesUseCase: GetCategoriesUseCase,
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
                        if (list.isNotEmpty()) {
                            consumeEvent(
                                HelpEvent.Internal.CategoriesLoaded(
                                    list.map { it.mapToUi() }
                                )
                            )
                        } else {
                            consumeEvent(
                                HelpEvent.Internal.ErrorLoaded(
                                    DataError.Unexpected()
                                )
                            )
                        }
                    }
                )
            }
            .launchIn(viewModelScope + exceptionHandler)
    }

    class Factory @Inject constructor(
        private val getCategoriesUseCase: GetCategoriesUseCase,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == HelpViewModel::class.java)
            return HelpViewModel(getCategoriesUseCase) as T
        }
    }
}
