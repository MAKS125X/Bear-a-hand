package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
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
        replay = 0,
        extraBufferCapacity = 1,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    val effects: SharedFlow<SideEffect>
        get() = _effects.asSharedFlow()

    private val _events: MutableSharedFlow<Event> = MutableSharedFlow(
        replay = 1,
        extraBufferCapacity = 15,
        onBufferOverflow = BufferOverflow.DROP_OLDEST
    )
    private val events: SharedFlow<Event>
        get() = _events.asSharedFlow()

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
        _events.tryEmit(event)
    }

    protected fun processSideEffect(sideEffect: SideEffect) {
        _effects.tryEmit(sideEffect)
    }

    abstract fun reduce(state: State, event: Event)

    companion object {
        const val SHARE_TIMEOUT_MILLIS: Long = 5000
    }
}
