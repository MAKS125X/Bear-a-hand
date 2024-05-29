package com.example.simbirsoftmobile.presentation.screens.search.events

import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.presentation.base.MviEvent
import com.example.simbirsoftmobile.presentation.base.MviSideEffect
import com.example.simbirsoftmobile.presentation.base.MviState
import com.example.simbirsoftmobile.presentation.base.UiText
import com.example.simbirsoftmobile.presentation.models.utils.getDescription
import com.example.simbirsoftmobile.presentation.screens.search.models.EventSearchUi

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
