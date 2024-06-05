package com.example.content_holder.screen

import com.example.core.models.event.EventModel
import com.example.dataerror.getDescription
import com.example.result.DataError
import com.example.ui.MviEvent
import com.example.ui.MviSideEffect
import com.example.ui.MviState
import com.example.ui.UiText

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
