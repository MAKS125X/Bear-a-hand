package com.example.simbirsoftmobile.presentation.screens.filter

import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.presentation.base.MviEvent
import com.example.simbirsoftmobile.presentation.base.MviSideEffect
import com.example.simbirsoftmobile.presentation.base.MviState
import com.example.simbirsoftmobile.presentation.base.UiText
import com.example.simbirsoftmobile.presentation.models.category.CategoryLongUi
import com.example.simbirsoftmobile.presentation.models.utils.getDescription

data class FilterState(
    val categories: List<CategoryLongUi> = emptyList(),
    val error: UiText? = null,
    val isLoading: Boolean = false,
) : MviState

sealed interface FilterEvent : MviEvent {
    sealed interface Ui : FilterEvent {
        class UpdateSelectedById(val id: String) : Ui

        data object AcceptChanges : Ui
    }

    sealed interface Internal : FilterEvent {
        data object LoadCategories : Internal

        data class ErrorLoaded(val error: UiText) : Internal {
            constructor(error: DataError) : this(error.getDescription())
        }

        data class CategoriesLoaded(val categories: List<CategoryLongUi>) : Internal
    }
}

sealed interface FilterSideEffect : MviSideEffect {
    data object SuccessChangesUpdate : FilterSideEffect

    data class ErrorChangesUpdate(val error: UiText) : FilterSideEffect {
        constructor(error: DataError) : this(error.getDescription())
    }
}
