package com.example.simbirsoftmobile.presentation.screens.help

import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.presentation.base.MviEvent
import com.example.simbirsoftmobile.presentation.base.MviSideEffect
import com.example.simbirsoftmobile.presentation.base.MviState
import com.example.simbirsoftmobile.presentation.base.UiText
import com.example.simbirsoftmobile.presentation.models.category.CategoryLongUi
import com.example.simbirsoftmobile.presentation.models.utils.getDescription

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
