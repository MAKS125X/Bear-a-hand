package com.example.auth.screen

import android.annotation.SuppressLint
import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.auth.di.AuthComponentViewModel
import com.example.auth.di.AuthDeps
import com.example.common_compose.theme.SimbirSoftMobileTheme
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class AuthFragment : Fragment() {
    @Inject
    lateinit var authDeps: AuthDeps

    private val viewModel: AuthViewModel by viewModels()

    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<AuthComponentViewModel>()
            .authComponent.inject(this)
        super.onAttach(context)
    }

    @SuppressLint("FlowOperatorInvokedInComposition")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state = viewModel.state.collectAsState()
                val sideEffect = viewModel.effects.distinctUntilChanged().collectAsState(AuthSideEffect.NoEffect)
                SimbirSoftMobileTheme {
                    AuthScreen(
                        modifier = Modifier.fillMaxSize(),
                        state = state.value,
                        viewModel::consumeEvent,
                        sideEffect.value,
                        { authDeps.authNavigation.onSuccessNavigation(parentFragmentManager) },
                    )
                }
            }
        }
    }

    companion object {
        const val TAG = "AuthFragment"

        @JvmStatic
        fun newInstance() = AuthFragment()
    }
}
