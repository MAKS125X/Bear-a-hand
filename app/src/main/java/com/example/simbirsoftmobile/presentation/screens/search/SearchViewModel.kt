package com.example.simbirsoftmobile.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simbirsoftmobile.presentation.base.MviViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@OptIn(FlowPreview::class)
class SearchViewModel : MviViewModel<SearchState, SearchSideEffect, SearchEvent>(
    SearchState()
) {
    private val queryFlow = state
        .map { it.searchQuery }
        .filter { str -> str.isNotBlank() }
        .distinctUntilChanged()
        .debounce(500)

    init {
        viewModelScope.launch {
            queryFlow.collect {
                processSideEffect(SearchSideEffect.SearchByQuery(it))
            }
        }
    }

    override fun reduce(state: SearchState, event: SearchEvent) {
        when (event) {
            is SearchEvent.Ui.UpdateSearchQuery -> {
                updateState(
                    state.copy(
                        searchQuery = event.query
                    )
                )
            }
        }
    }

    class Factory @Inject constructor() : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == SearchViewModel::class.java)
            return SearchViewModel() as T
        }
    }
}
