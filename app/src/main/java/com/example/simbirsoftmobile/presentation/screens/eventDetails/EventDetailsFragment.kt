package com.example.simbirsoftmobile.presentation.screens.eventDetails

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.flowWithLifecycle
import androidx.lifecycle.lifecycleScope
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentEventDetailsBinding
import com.example.simbirsoftmobile.domain.core.DataError
import com.example.simbirsoftmobile.domain.core.Either
import com.example.simbirsoftmobile.domain.usecases.GetEventDetailsUseCase
import com.example.simbirsoftmobile.presentation.models.event.EventLongUi
import com.example.simbirsoftmobile.presentation.models.event.mapToLongUi
import com.example.simbirsoftmobile.presentation.screens.utils.UiState
import com.example.simbirsoftmobile.presentation.screens.utils.getRemainingDateInfo
import com.example.simbirsoftmobile.presentation.screens.utils.getSingleParcelableFromBundleByKey
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class EventDetailsFragment : Fragment() {
    private var _binding: FragmentEventDetailsBinding? = null
    private val binding: FragmentEventDetailsBinding
        get() = _binding!!

    private var eventId: String? = null
    private var uiState: UiState<EventLongUi> = UiState.Idle

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)

        val currentState = uiState
        if (currentState is UiState.Success) {
            outState.putParcelable(EVENT_MODEL_KEY, currentState.data)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            eventId = it.getString(EVENT_ID_KEY)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun updateUiState() {
        with(binding) {
            when (val currentState = uiState) {
                is UiState.Error -> {
                    progressIndicator.visibility = View.GONE
                    eventDetailsLayout.visibility = View.GONE
                    errorTV.visibility = View.VISIBLE
                    errorTV.text = currentState.message
                }

                UiState.Idle -> {}

                UiState.Loading -> {
                    progressIndicator.visibility = View.VISIBLE
                    eventDetailsLayout.visibility = View.GONE
                    errorTV.visibility = View.GONE
                }

                is UiState.Success -> {
                    errorTV.visibility = View.GONE
                    progressIndicator.visibility = View.GONE
                    eventDetailsLayout.visibility = View.VISIBLE

                    titleTV.text = currentState.data.name
                    organizerNameTV.text = currentState.data.organization
                    addressTV.text = currentState.data.address

                    remainDateTV.text =
                        getRemainingDateInfo(
                            currentState.data.startDate,
                            currentState.data.endDate,
                            requireContext(),
                        )

                    initEmailSection(currentState.data.email)
                    initPhoneNumbers(currentState.data.phone)
                    initImage(currentState.data.photo)

                    descriptionTV.text = currentState.data.description
                    initOrganizerUrlText(currentState.data.url)

                    toolbar.setOnMenuItemClickListener {
                        when (it.itemId) {
                            R.id.share_event -> {
                                val shareIntent =
                                    Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(Intent.EXTRA_TEXT, currentState.data.name)
                                        type = "text/plain"
                                    }
                                startActivity(
                                    Intent.createChooser(
                                        shareIntent,
                                        "Поделиться событием",
                                    )
                                )
                            }
                        }
                        true
                    }
                }
            }
        }
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)

        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }

        if (savedInstanceState != null) {
            val currentEvent =
                getSingleParcelableFromBundleByKey<EventLongUi>(savedInstanceState, EVENT_MODEL_KEY)
            if (currentEvent != null) {
                uiState = UiState.Success(currentEvent)
                updateUiState()
            }
        } else {
            val currentId = eventId
            if (currentId != null) {
                getEventById(currentId)
            } else {
                UiState.Error(getString(R.string.error_occurred_while_receiving_data))
                updateUiState()
            }
        }
    }

    private fun getEventById(id: String) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            uiState = UiState.Error(getString(R.string.unexpected_error))
            updateUiState()
        }

        lifecycleScope.launch(exceptionHandler) {
            GetEventDetailsUseCase()
                .invoke(id)
                .flowWithLifecycle(viewLifecycleOwner.lifecycle, Lifecycle.State.STARTED)
                .onEach {
                    uiState = UiState.Loading
                    updateUiState()
                }
                .collect{
                    uiState = when (it) {
                        is Either.Left -> {
                            UiState.Error(
                                when (it.value) {
                                    is DataError.Api -> it.value.error
                                        ?: getString(R.string.error_occurred_while_receiving_data)
                                    is DataError.InvalidParameters -> getString(R.string.unexpected_error)
                                    DataError.Timeout -> getString(R.string.timeout_error)
                                    is DataError.Unexpected -> getString(R.string.unexpected_error)
                                    is DataError.Connection -> getString(R.string.connection_error)
                                    DataError.NetworkBlock -> getString(R.string.unexpected_error)
                                }
                            )
                        }

                        is Either.Right -> {
                            UiState.Success(
                                it.value.mapToLongUi()
                            )
                        }
                    }
                    updateUiState()
                }
        }
    }

    private fun initEmailSection(email: String) {
        if (email.isNotBlank()) {
            binding.emailLayout.visibility = View.VISIBLE
            binding.sendEmailTV.setOnClickListener {
                sendEmail(email)
            }
        } else {
            binding.emailLayout.visibility = View.GONE
        }
    }

    private fun sendEmail(email: String) {
        val subject = getString(R.string.charitable_assistance)
        val intent =
            Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:")
                putExtra(Intent.EXTRA_EMAIL, arrayOf(email))
                putExtra(Intent.EXTRA_SUBJECT, subject)
            }
        if (intent.resolveActivity(requireActivity().packageManager) != null) {
            startActivity(intent)
        }
    }

    private fun initPhoneNumbers(phoneNumber: String) {
        if (phoneNumber.isNotEmpty()) {
            binding.emailLayout.visibility = View.VISIBLE
            binding.phoneTV.text = phoneNumber
        } else {
            binding.emailLayout.visibility = View.GONE
        }
    }

    private fun initImage(imageSource: String) {
        with(binding) {
            val imageLoader = ImageLoader(previewIV.context)
            val request: ImageRequest = ImageRequest.Builder(previewIV.context)
                .data(imageSource)
                .placeholder(R.drawable.loading_animation)
                .target(
                    onStart = { placeholder ->
                        previewIV.visibility = View.VISIBLE
                        previewIV.setImageDrawable(placeholder)
                    },
                    onSuccess = { drawable ->
                        previewIV.visibility = View.VISIBLE
                        previewIV.setImageDrawable(drawable)
                    },
                    onError = {
                        previewIV.visibility = View.GONE
                    }
                )
                .build()

            imageLoader.enqueue(request)

            previewIV.load(imageSource) {
                placeholder(R.drawable.loading_animation)
            }
        }
    }

    private fun initOrganizerUrlText(siteUrl: String) {
        with(binding) {
            if (siteUrl.isNotBlank()) {
                organizerUrlTV.visibility = View.VISIBLE
                organizerUrlTV.setOnClickListener {
                    openLink(siteUrl)
                }
            } else {
                organizerUrlTV.visibility = View.GONE
            }
        }
    }

    private fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "EventDetailsFragment"
        const val EVENT_ID_KEY = "eventId"
        private const val EVENT_MODEL_KEY = "EventModel"

        @JvmStatic
        fun newInstance(eventId: String) =
            EventDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(EVENT_ID_KEY, eventId)
                }
            }
    }
}
