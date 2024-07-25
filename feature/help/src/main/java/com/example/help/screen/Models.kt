package com.example.help.screen

import com.example.dataerror.getDescription
import com.example.help.model.CategoryLongUi
import com.example.result.DataError
import com.example.ui.MviEvent
import com.example.ui.MviSideEffect
import com.example.ui.MviState
import com.example.ui.UiText

data class HelpState(
    val categories: List<CategoryLongUi> = emptyList(),
    val isLoading: Boolean = true,
    val error: UiText? = null,
) : MviState

sealed interface HelpEvent : MviEvent {
    sealed interface Ui : HelpEvent

    sealed interface Internal : HelpEvent {
        data object LoadCategories : Internal
        data class CategoriesLoaded(val categories: List<CategoryLongUi>) : Internal
        data class ErrorLoaded(val error: UiText?) : Internal {
            constructor(error: DataError) : this(error.getDescription())
        }
    }
}

sealed interface HelpSideEffect : MviSideEffect
