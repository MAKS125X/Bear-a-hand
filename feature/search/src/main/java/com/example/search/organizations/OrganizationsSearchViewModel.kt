package com.example.search.organizations

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.result.processResult
import com.example.search.R
import com.example.search.models.toOrganizationSearchUi
import com.example.search.usecase.SearchOrganizationsUseCase
import com.example.ui.MviViewModel
import com.example.ui.UiText
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import javax.inject.Inject
import javax.inject.Provider

class OrganizationsSearchViewModel(
    private val searchOrganizationsUseCase: SearchOrganizationsUseCase,
) : MviViewModel<OrganizationSearchState, OrganizationSearchSideEffect, OrganizationSearchEvent>(
    OrganizationSearchState()
) {
    override fun reduce(state: OrganizationSearchState, event: OrganizationSearchEvent) {
        when (event) {
            is OrganizationSearchEvent.Internal.ErrorLoaded -> {
                updateState(
                    state.copy(
                        showInfo = false,
                        isLoading = false,
                        error = event.error,
                        organizations = emptyList(),
                        resultInfo = null,
                    )
                )
            }

            is OrganizationSearchEvent.Internal.OrganizationLoaded -> {
                updateState(
                    state.copy(
                        showInfo = false,
                        isLoading = false,
                        organizations = event.organizations,
                        resultInfo = if (event.organizations.isNotEmpty())
                            UiText.StringResource(
                                R.string.search_result_events_size,
                                event.organizations.size.toString(),
                            ) else
                            UiText.StringResource(R.string.empty_search_result),
                    )
                )
            }

            is OrganizationSearchEvent.Ui.LoadOrganizations -> {
                updateState(
                    state.copy(
                        showInfo = false,
                        isLoading = true,
                        resultInfo = null,
                    )
                )

                searchEvents(event.query)
            }
        }
    }

    private fun searchEvents(query: String) {
        searchOrganizationsUseCase
            .invoke(query)
            .onEach { either ->
                either.processResult(
                    onError = {
                        consumeEvent(OrganizationSearchEvent.Internal.ErrorLoaded(it))
                    },
                    onSuccess = { list ->
                        consumeEvent(
                            OrganizationSearchEvent.Internal.OrganizationLoaded(
                                list.map { it.toOrganizationSearchUi() })
                        )
                    }
                )
            }
            .launchIn(viewModelScope)
    }

    class Factory @Inject constructor(
        private val searchOrganizationsUseCase: Provider<SearchOrganizationsUseCase>,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == OrganizationsSearchViewModel::class.java)
            return OrganizationsSearchViewModel(searchOrganizationsUseCase.get()) as T
        }
    }
}
