package com.example.event_details.screen

import com.example.dataerror.getDescription
import com.example.event_details.models.EventLongUi
import com.example.result.DataError
import com.example.ui.MviEvent
import com.example.ui.MviSideEffect
import com.example.ui.MviState
import com.example.ui.UiText

data class EventDetailsState(
    val isLoading: Boolean = false,
    val eventDetails: EventLongUi? = null,
    val error: UiText? = null,
) : MviState

sealed interface EventDetailsEvent : MviEvent {
    sealed interface Ui : EventDetailsEvent
    sealed interface Internal : EventDetailsEvent {
        data class LoadEventDetails(val eventId: String) : Internal
        data class DetailsLoaded(val eventDetails: EventLongUi) : Internal
        data class ErrorLoading(val error: UiText) : Internal {
            constructor(error: DataError) : this(error.getDescription())
        }
    }
}

interface EventDetailsSideEffect : MviSideEffect
