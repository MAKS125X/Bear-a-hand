package com.example.search.events

import com.example.dataerror.getDescription
import com.example.result.DataError
import com.example.search.models.EventSearchUi
import com.example.ui.MviEvent
import com.example.ui.MviSideEffect
import com.example.ui.MviState
import com.example.ui.UiText

data class EventSearchState(
    val events: List<EventSearchUi> = emptyList(),
    val error: UiText? = null,
    val isLoading: Boolean = false,
    val showInfo: Boolean = true,
    val resultInfo: UiText? = null,
) : MviState

sealed interface EventSearchEvent : MviEvent {
    sealed interface Ui : EventSearchEvent {
        data class LoadEvents(val query: String) : Ui
    }

    sealed interface Internal : EventSearchEvent {
        data class EventsLoaded(val events: List<EventSearchUi>) : Internal

        data class ErrorLoaded(val error: UiText) : Internal {
            constructor(error: DataError) : this(error.getDescription())
        }
    }
}

sealed interface EventSearchSideEffect : MviSideEffect
