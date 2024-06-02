package com.example.simbirsoftmobile.presentation.screens.news

import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.presentation.base.MviEvent
import com.example.simbirsoftmobile.presentation.base.MviSideEffect
import com.example.simbirsoftmobile.presentation.base.MviState
import com.example.simbirsoftmobile.presentation.base.UiText
import com.example.simbirsoftmobile.presentation.models.event.EventShortUi

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
