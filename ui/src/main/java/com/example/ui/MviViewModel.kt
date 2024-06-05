package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.shareIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

abstract class MviViewModel<State : MviState, SideEffect : MviSideEffect, Event : MviEvent>(
    initialState: State,
) : ViewModel() {
    private val _state: MutableStateFlow<State> = MutableStateFlow(initialState)
    val state: StateFlow<State>
        get() = _state
            .asStateFlow()

    private val _effects: MutableSharedFlow<SideEffect> = MutableSharedFlow(
        replay = 1,
        extraBufferCapacity = 0,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effects: SharedFlow<SideEffect>
        get() = _effects.asSharedFlow()

    private val _events = Channel<Event>(Channel.CONFLATED)
    private val events: SharedFlow<Event> = _events.receiveAsFlow().shareIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(SHARE_TIMEOUT_MILLIS),
        replay = 1,
    )

    init {
        viewModelScope.launch {
            events.collect {
                reduce(state.value, it)
            }
        }
    }

    protected fun updateState(state: State) {
        _state.update { state }
    }

    fun consumeEvent(event: Event) {
        _events.trySend(event)
    }

    protected fun processSideEffect(sideEffect: SideEffect) {
        _effects.tryEmit(sideEffect)
    }

    abstract fun reduce(state: State, event: Event)

    companion object {
        const val SHARE_TIMEOUT_MILLIS: Long = 5000
    }
}
