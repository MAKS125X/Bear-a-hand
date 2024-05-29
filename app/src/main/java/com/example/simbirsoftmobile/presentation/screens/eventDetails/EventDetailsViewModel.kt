package com.example.simbirsoftmobile.presentation.screens.eventDetails

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.usecases.GetEventDetailsUseCase
import com.example.simbirsoftmobile.domain.utils.processResult
import com.example.simbirsoftmobile.presentation.base.MviViewModel
import com.example.simbirsoftmobile.presentation.models.event.mapToLongUi
import com.example.simbirsoftmobile.presentation.models.utils.getDescription
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus

class EventDetailsViewModel(
    savedStateHandle: SavedStateHandle,
) : MviViewModel<EventDetailsState, EventDetailsSideEffect, EventDetailsEvent>(
    EventDetailsState(),
) {

    init {
        val id = savedStateHandle.get<String>(EventDetailsFragment.EVENT_ID_KEY)
        if (id != null) {
            consumeEvent(
                EventDetailsEvent.Internal.LoadEventDetails(id)
            )
        } else {
            consumeEvent(EventDetailsEvent.Internal.ErrorLoading(DataError.Unexpected()))
        }
    }

    override fun reduce(state: EventDetailsState, event: EventDetailsEvent) {
        when (event) {
            is EventDetailsEvent.Internal.DetailsLoaded -> {
                updateState(
                    state.copy(
                        eventDetails = event.eventDetails,
                        isLoading = false,
                        error = null,
                    )
                )
            }

            is EventDetailsEvent.Internal.ErrorLoading -> {
                updateState(
                    state.copy(
                        eventDetails = null,
                        isLoading = false,
                        error = event.error,
                    )
                )
            }

            is EventDetailsEvent.Internal.LoadEventDetails -> {
                updateState(
                    state.copy(
                        isLoading = true,
                        error = null,
                        eventDetails = null,
                    )
                )
                getEventById(event.eventId)
            }
        }
    }

    private fun getEventById(id: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            consumeEvent(EventDetailsEvent.Internal.ErrorLoading(DataError.Unexpected()))
        }

        GetEventDetailsUseCase()
            .invoke(id)
            .onEach { either ->
                either.processResult(
                    onError = {
                        consumeEvent(EventDetailsEvent.Internal.ErrorLoading(it.getDescription()))
                    },
                    onSuccess = { event ->
                        consumeEvent(
                            EventDetailsEvent.Internal.DetailsLoaded(
                                eventDetails = event.mapToLongUi()
                            )
                        )
                    }
                )
            }
            .launchIn(viewModelScope + exceptionHandler)
    }
}
