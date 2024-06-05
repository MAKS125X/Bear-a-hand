package com.example.search.organizations

import com.example.dataerror.getDescription
import com.example.result.DataError
import com.example.search.models.OrganizationSearchUi
import com.example.ui.MviEvent
import com.example.ui.MviSideEffect
import com.example.ui.MviState
import com.example.ui.UiText

data class OrganizationSearchState(
    val organizations: List<OrganizationSearchUi> = emptyList(),
    val error: UiText? = null,
    val isLoading: Boolean = false,
    val showInfo: Boolean = true,
    val resultInfo: UiText? = null,
) : MviState

sealed interface OrganizationSearchEvent : MviEvent {
    sealed interface Ui : OrganizationSearchEvent {
        data class LoadOrganizations(val query: String) : Ui
    }

    sealed interface Internal : OrganizationSearchEvent {
        data class OrganizationLoaded(val organizations: List<OrganizationSearchUi>) : Internal
        data class ErrorLoaded(val error: UiText) : Internal {
            constructor(error: DataError) : this(error.getDescription())
        }
    }
}

sealed interface OrganizationSearchSideEffect : MviSideEffect
