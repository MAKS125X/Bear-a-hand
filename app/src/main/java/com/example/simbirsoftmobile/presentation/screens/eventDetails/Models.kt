package com.example.simbirsoftmobile.presentation.screens.eventDetails

import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.presentation.base.MviEvent
import com.example.simbirsoftmobile.presentation.base.MviSideEffect
import com.example.simbirsoftmobile.presentation.base.MviState
import com.example.simbirsoftmobile.presentation.base.UiText
import com.example.simbirsoftmobile.presentation.models.event.EventLongUi
import com.example.simbirsoftmobile.presentation.models.utils.getDescription

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
