package com.example.simbirsoftmobile.presentation.screens.search.events

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.usecases.SearchEventsUseCase
import com.example.simbirsoftmobile.domain.utils.processResult
import com.example.simbirsoftmobile.presentation.base.MviViewModel
import com.example.simbirsoftmobile.presentation.base.UiText
import com.example.simbirsoftmobile.presentation.screens.search.models.toEventSearchUi
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import javax.inject.Inject

class EventSearchViewModel(
    private val searchEventsUseCase: SearchEventsUseCase,
) : MviViewModel<EventSearchState, EventSearchSideEffect, EventSearchEvent>(
    EventSearchState(),
) {
    override fun reduce(state: EventSearchState, event: EventSearchEvent) {
        when (event) {
            is EventSearchEvent.Internal.ErrorLoaded -> {
                updateState(
                    state.copy(
                        showInfo = false,
                        isLoading = false,
                        error = event.error,
                        events = emptyList(),
                        resultInfo = null,
                    )
                )
            }

            is EventSearchEvent.Internal.EventsLoaded -> {
                updateState(
                    state.copy(
                        showInfo = false,
                        isLoading = false,
                        events = event.events,
                        resultInfo = if (event.events.isNotEmpty())
                            UiText.StringResource(
                                R.string.search_result_events_size,
                                event.events.size.toString(),
                            )
                        else UiText.StringResource(R.string.empty_search_result),
                    )
                )
            }

            is EventSearchEvent.Ui.LoadEvents -> {
                updateState(
                    state.copy(
                        showInfo = false,
                        isLoading = true,
                        resultInfo = null,
                    )
                )

                searchEvents(event.query)
            }
        }
    }

    private fun searchEvents(query: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            consumeEvent(EventSearchEvent.Internal.ErrorLoaded(DataError.Unexpected()))
        }

        searchEventsUseCase
            .invoke(query)
            .onEach { either ->
                either.processResult(
                    onError = {
                        consumeEvent(EventSearchEvent.Internal.ErrorLoaded(it))
                    },
                    onSuccess = { list ->
                        consumeEvent(
                            EventSearchEvent.Internal.EventsLoaded(
                                list.map { it.toEventSearchUi() })
                        )
                    }
                )
            }
            .launchIn(viewModelScope + exceptionHandler)
    }

    class Factory @Inject constructor(
        private val searchEventsUseCase: SearchEventsUseCase,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == EventSearchViewModel::class.java)
            return EventSearchViewModel(searchEventsUseCase) as T
        }
    }
}
