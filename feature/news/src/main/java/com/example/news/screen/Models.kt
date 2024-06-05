package com.example.news.screen

import com.example.news.models.EventShortUi
import com.example.result.DataError
import com.example.ui.MviEvent
import com.example.ui.MviSideEffect
import com.example.ui.MviState
import com.example.ui.UiText

data class NewsState(
    val isLoading: Boolean = false,
    val news: List<EventShortUi> = emptyList(),
    val error: UiText? = null,
) : MviState

sealed interface NewsEvent : MviEvent {
    sealed interface Ui : NewsEvent

    sealed interface Internal : NewsEvent {
        data object LoadNews : Internal
        data class NewsLoaded(val list: List<EventShortUi>) : Internal
        data class ErrorLoaded(val error: DataError) : Internal
    }
}

sealed interface NewsSideEffect : MviSideEffect
