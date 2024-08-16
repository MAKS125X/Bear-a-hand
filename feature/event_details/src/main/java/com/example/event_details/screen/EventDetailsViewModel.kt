package com.example.event_details.screen

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.dataerror.getDescription
import com.example.event_details.models.mapToLongUi
import com.example.event_details.usecase.GetEventDetailsUseCase
import com.example.result.DataError
import com.example.result.processResult
import com.example.ui.MviViewModel

import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import javax.inject.Inject
import javax.inject.Provider

class EventDetailsViewModel(
    private val getEventDetailsUseCase: GetEventDetailsUseCase,
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
            consumeEvent(EventDetailsEvent.Internal.ErrorLoaded(DataError.Unexpected()))
        }
    }

    override fun reduce(state: EventDetailsState, event: EventDetailsEvent) {
        when (event) {
            is EventDetailsEvent.Internal.EventDetailsLoaded -> {
                updateState(
                    state.copy(
                        eventDetails = event.eventDetails,
                        isLoading = false,
                        error = null,
                    )
                )
            }

            is EventDetailsEvent.Internal.ErrorLoaded -> {
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
            consumeEvent(EventDetailsEvent.Internal.ErrorLoaded(DataError.Unexpected()))
        }

        getEventDetailsUseCase
            .invoke(id)
            .onEach { either ->
                either.processResult(
                    onError = {
                        consumeEvent(EventDetailsEvent.Internal.ErrorLoaded(it.getDescription()))
                    },
                    onSuccess = { event ->
                        consumeEvent(
                            EventDetailsEvent.Internal.EventDetailsLoaded(
                                eventDetails = event.mapToLongUi()
                            )
                        )
                    }
                )
            }
            .launchIn(viewModelScope + exceptionHandler)
    }

    open class Factory @Inject constructor(
        private val getEventDetailsUseCase: Provider<GetEventDetailsUseCase>,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            require(modelClass == EventDetailsViewModel::class.java)
            return EventDetailsViewModel(
                getEventDetailsUseCase.get(),
                extras.createSavedStateHandle(),
            ) as T
        }
    }
}
