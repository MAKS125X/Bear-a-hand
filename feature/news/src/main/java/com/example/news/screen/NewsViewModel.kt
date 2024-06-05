package com.example.news.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.dataerror.getDescription
import com.example.news.models.mapToShorUi
import com.example.news.useCase.GetEventsBySettingsUseCase
import com.example.result.DataError
import com.example.result.processResult
import com.example.ui.MviViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import javax.inject.Inject
import javax.inject.Provider

class NewsViewModel(
    private val getEventsBySettingsUseCase: GetEventsBySettingsUseCase,
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

    class Factory @Inject constructor(
        private val getEventsBySettingsUseCase: Provider<GetEventsBySettingsUseCase>,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == NewsViewModel::class.java)
            return NewsViewModel(getEventsBySettingsUseCase.get()) as T
        }
    }
}
