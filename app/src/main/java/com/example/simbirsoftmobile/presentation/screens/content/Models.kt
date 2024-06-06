package com.example.simbirsoftmobile.presentation.screens.content

import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.models.event.EventModel
import com.example.simbirsoftmobile.presentation.base.MviEvent
import com.example.simbirsoftmobile.presentation.base.MviSideEffect
import com.example.simbirsoftmobile.presentation.base.MviState
import com.example.simbirsoftmobile.presentation.base.UiText
import com.example.simbirsoftmobile.presentation.models.utils.getDescription

data class ContentState(
    val badge: Int = 0,
    private val readEventIds: MutableSet<String> = mutableSetOf(),
    private var currentEventList: List<EventModel> = emptyList(),
) : MviState


sealed interface ContentEvent : MviEvent {
    sealed interface Ui : ContentEvent

    sealed interface Internal : ContentEvent {
        data class UpdateBadge(val value: Int) : Internal
        data class ErrorUpdate(val error: UiText) : Internal {
            constructor(error: DataError) : this(error.getDescription())
        }
    }
}

sealed interface ContentSideEffect : MviSideEffect
