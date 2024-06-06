package com.example.simbirsoftmobile.presentation.screens.search.organizations

import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.presentation.base.MviEvent
import com.example.simbirsoftmobile.presentation.base.MviSideEffect
import com.example.simbirsoftmobile.presentation.base.MviState
import com.example.simbirsoftmobile.presentation.base.UiText
import com.example.simbirsoftmobile.presentation.models.utils.getDescription
import com.example.simbirsoftmobile.presentation.screens.search.models.OrganizationSearchUi

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
