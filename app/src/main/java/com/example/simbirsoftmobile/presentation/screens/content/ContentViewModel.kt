package com.example.simbirsoftmobile.presentation.screens.content

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.usecases.GetUnreadEventsCountUseCase
import com.example.simbirsoftmobile.domain.utils.processResult
import com.example.simbirsoftmobile.presentation.base.MviViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.plus
import javax.inject.Inject

class ContentViewModel(
    private val getUnreadEventsCountUseCase: GetUnreadEventsCountUseCase,
) : MviViewModel<ContentState, ContentSideEffect, ContentEvent>(
    ContentState()
) {
    init {
        observeBadgeValue()
    }

    override fun reduce(state: ContentState, event: ContentEvent) {
        when (event) {
            is ContentEvent.Internal.UpdateBadge -> updateState(
                state.copy(
                    badge = event.value,
                )
            )

            is ContentEvent.Internal.ErrorUpdate -> {
                state.copy(
                    badge = 0,
                )
            }
        }
    }

    private fun observeBadgeValue() {
        val exceptionHandler = CoroutineExceptionHandler { _, e ->
            consumeEvent(ContentEvent.Internal.ErrorUpdate(DataError.Unexpected()))
        }

        getUnreadEventsCountUseCase()
            .onEach {
                Log.d("Badge", "observeBadgeValue: $it")
                it.processResult({
                    consumeEvent(ContentEvent.Internal.ErrorUpdate(it))
                }, {
                    consumeEvent(ContentEvent.Internal.UpdateBadge(it))
                })
            }
            .launchIn(viewModelScope + exceptionHandler)
    }

    class Factory @Inject constructor(
        private val getUnreadEventsCountUseCase: GetUnreadEventsCountUseCase,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            require(modelClass == ContentViewModel::class.java)
            return ContentViewModel(getUnreadEventsCountUseCase) as T
        }
    }
}