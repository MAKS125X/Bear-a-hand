package com.example.search

import com.example.ui.MviEvent
import com.example.ui.MviSideEffect
import com.example.ui.MviState

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
