package com.example.simbirsoftmobile.presentation.screens.search

import com.example.simbirsoftmobile.presentation.base.MviEvent
import com.example.simbirsoftmobile.presentation.base.MviSideEffect
import com.example.simbirsoftmobile.presentation.base.MviState

data class SearchState(
    val searchQuery: String = "",
) : MviState

sealed interface SearchEvent : MviEvent {
    sealed interface Ui : SearchEvent {
        data class UpdateSearchQuery(val query: String) : Ui
    }

    sealed interface Internal : SearchEvent
}

sealed interface SearchSideEffect : MviSideEffect {
    data class SearchByQuery(val query: String) : SearchSideEffect
}
