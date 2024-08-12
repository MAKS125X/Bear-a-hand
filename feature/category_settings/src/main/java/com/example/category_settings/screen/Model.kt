package com.example.category_settings.screen

import com.example.category_settings.model.SettingUi
import com.example.dataerror.getDescription
import com.example.result.DataError
import com.example.ui.MviEvent
import com.example.ui.MviSideEffect
import com.example.ui.MviState
import com.example.ui.UiText

data class FilterState(
    val categories: List<SettingUi> = emptyList(),
    val error: UiText? = null,
    val isLoading: Boolean = true,
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

        data class CategoriesLoaded(val categories: List<SettingUi>) : Internal
    }
}

sealed interface FilterSideEffect : MviSideEffect {
    data object SuccessChangesUpdate : FilterSideEffect

    data class ErrorChangesUpdate(val error: UiText) : FilterSideEffect {
        constructor(error: DataError) : this(error.getDescription())
    }
}
