package com.example.news.screen

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
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import com.example.common_compose.theme.SimbirSoftMobileTheme
import com.example.news.di.NewsComponentViewModel
import com.example.news.di.NewsDeps
import dagger.Lazy
import kotlinx.coroutines.flow.distinctUntilChanged
import javax.inject.Inject

class NewsFragment : Fragment() {
    @Inject
    lateinit var newsDeps: NewsDeps

    @Inject
    lateinit var factory: Lazy<NewsViewModel.Factory>

    private val viewModel: NewsViewModel by activityViewModels {
        factory.get()
    }

    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<NewsComponentViewModel>()
            .newDetailsComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        val sideEffectFlow =
            viewModel.effects.distinctUntilChanged()
        return ComposeView(requireContext()).apply {
            setViewCompositionStrategy(ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed)
            setContent {
                val state = viewModel.state.collectAsState()
                val sideEffect = sideEffectFlow.collectAsState(NewsSideEffect.NoEffect)
                if (savedInstanceState == null){
                    viewModel.consumeEvent(NewsEvent.Internal.LoadNews)
                }

                SimbirSoftMobileTheme {
                    NewsScreen(
                        modifier = Modifier.fillMaxSize(),
                        state = state.value,
                        onEvent = viewModel::consumeEvent,
                        sideEffect = sideEffect.value,
                        navigateToEventDetails = this@NewsFragment::moveToEventDetailsFragment,
                        navigateToSettings = {
                            newsDeps.newsComponentNavigation.navigateToSettings(
                                parentFragmentManager
                            )
                        },
                    )
                }
            }
        }
    }

    private fun moveToEventDetailsFragment(eventId: String) {
        newsDeps.newsComponentNavigation.navigateToEventDetails(parentFragmentManager, eventId)
    }

    companion object {
        const val TAG = "NewsFragment"

        @JvmStatic
        fun newInstance() = NewsFragment()
    }
}
