package com.example.simbirsoftmobile.presentation.screens.news

import androidx.lifecycle.viewModelScope
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.usecases.GetEventsBySettingsUseCase
import com.example.simbirsoftmobile.domain.utils.processResult
import com.example.simbirsoftmobile.presentation.base.MviViewModel
import com.example.simbirsoftmobile.presentation.models.event.mapToShorUi
import com.example.simbirsoftmobile.presentation.models.utils.getDescription
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus

class NewsViewModel(
    private val getEventsBySettingsUseCase: GetEventsBySettingsUseCase = GetEventsBySettingsUseCase()
) : MviViewModel<NewsState, NewsSideEffect, NewsEvent>(
    NewsState(),
) {
    init {
        consumeEvent(NewsEvent.Internal.LoadNews)
    }

    override fun reduce(state: NewsState, event: NewsEvent) {
        when (event) {
            is NewsEvent.Internal.ErrorLoaded -> {
                updateState(
                    state.copy(
                        isLoading = false,
                        error = event.error.getDescription(),
                    )
                )
            }

            is NewsEvent.Internal.NewsLoaded -> {
                updateState(
                    state.copy(
                        isLoading = false,
                        news = event.list,
                        error = null,
                    )
                )
            }

            NewsEvent.Internal.LoadNews -> {
                updateState(
                    state.copy(
                        isLoading = true,
                        error = null,
                    )
                )
                loadNews()
            }
        }
    }

    private fun loadNews() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            consumeEvent(NewsEvent.Internal.ErrorLoaded(DataError.Unexpected()))
        }

        getEventsBySettingsUseCase.invoke()
            .onEach { either ->
                either.processResult(
                    onError = {
                        consumeEvent(NewsEvent.Internal.ErrorLoaded(it))
                    },
                    onSuccess = { list ->
                        consumeEvent(
                            NewsEvent.Internal.NewsLoaded(
                                list.map { it.mapToShorUi() }
                            )
                        )
                    }
                )
            }
            .launchIn(scope = viewModelScope + exceptionHandler)
    }
}
