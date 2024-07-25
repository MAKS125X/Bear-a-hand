package com.example.ui

import android.os.Bundle
import android.view.View
import androidx.annotation.CallSuper
import androidx.annotation.LayoutRes
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

abstract class MviFragment<State : MviState, SideEffect : MviSideEffect, Event : MviEvent> :
    Fragment {

    constructor() : super()

    constructor(@LayoutRes contentLayoutId: Int) : super(contentLayoutId)

    open val initialEvent: Event? = null

    protected abstract val viewModel: MviViewModel<State, SideEffect, Event>

    private fun collectSideEffects() {
        viewModel.effects
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .onEach { handleSideEffects(effect = it) }
            .launchIn(lifecycleScope)
    }

    private fun collectState() {
        viewModel.state
            .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
            .distinctUntilChanged()
            .onEach { renderState(state = it) }
            .launchIn(lifecycleScope)
    }

    open fun handleSideEffects(effect: SideEffect) {}

    open fun renderState(state: State) {}

    @CallSuper
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            initialEvent?.let {
                viewModel.consumeEvent(it)
            }
        }
    }

    @CallSuper
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        collectSideEffects()
        collectState()
    }
}
