package com.example.simbirsoftmobile.presentation.screens.eventDetails

import android.content.Intent
import android.content.res.Resources
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.simbirsoftmobile.R
import com.example.simbirsoftmobile.databinding.FragmentEventDetailsBinding
import com.example.simbirsoftmobile.presentation.models.event.Event
import com.example.simbirsoftmobile.presentation.screens.utils.UiState
import com.example.simbirsoftmobile.presentation.screens.utils.getRemainingDateInfo
import com.example.simbirsoftmobile.repository.EventRepository
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.Future

class EventDetailsFragment : Fragment() {
    private var _binding: FragmentEventDetailsBinding? = null
    private val binding: FragmentEventDetailsBinding
        get() = _binding!!

    private val executor: ExecutorService = Executors.newSingleThreadExecutor()
    private var downloadTask: Future<*>? = null

    private var eventId: Int? = null
    private var uiState: UiState<Event> = UiState.Idle

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
            eventId = it.getInt(EVENT_ID_KEY)
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
                    progressIndicator.post {
                        progressIndicator.visibility = View.GONE
                    }
                    eventDetailsLayout.post {
                        eventDetailsLayout.visibility = View.GONE
                    }
                    errorTV.post {
                        errorTV.visibility = View.VISIBLE
                        errorTV.text = currentState.message
                    }
                }

                UiState.Idle -> {}

                UiState.Loading -> {
                    progressIndicator.post {
                        progressIndicator.visibility = View.VISIBLE
                    }
                    eventDetailsLayout.post {
                        eventDetailsLayout.visibility = View.GONE
                    }
                    errorTV.post {
                        errorTV.visibility = View.GONE
                    }
                }

                is UiState.Success -> {
                    errorTV.post {
                        errorTV.visibility = View.GONE
                    }
                    progressIndicator.post {
                        progressIndicator.visibility = View.GONE
                    }
                    eventDetailsLayout.post {
                        eventDetailsLayout.visibility = View.VISIBLE

                        titleTV.text = currentState.data.title
                        organizerNameTV.text = currentState.data.organizerName
                        addressTV.text = currentState.data.address

                        remainDateTV.text =
                            getRemainingDateInfo(
                                currentState.data.dateStart,
                                currentState.data.dateEnd,
                                requireContext(),
                            )

                        initEmailSection(currentState.data.email)
                        initPhoneNumbers(currentState.data.phoneNumbers)
                        initImage(currentState.data.imageUrl)

                        descriptionTV.text = currentState.data.description
                        initOrganizerUrlText(currentState.data.siteUrl)

                        toolbar.setOnMenuItemClickListener {
                            when (it.itemId) {
                                R.id.share_event -> {
                                    val shareIntent =
                                        Intent().apply {
                                            action = Intent.ACTION_SEND
                                            putExtra(Intent.EXTRA_TEXT, currentState.data.title)
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
    }

    private fun getEventFromBundle(savedInstanceState: Bundle): Event? =
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            savedInstanceState.getParcelable(EVENT_MODEL_KEY, Event::class.java)
        } else {
            @Suppress("DEPRECATION")
            savedInstanceState.getParcelable<Event>(EVENT_MODEL_KEY)
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
            val currentEvent = getEventFromBundle(savedInstanceState)
            if (currentEvent != null) {
                uiState = UiState.Success(currentEvent)
                updateUiState()
            }
        } else {
            uiState = UiState.Loading

            updateUiState()
            val loadList = Runnable {
                val event = eventId?.let { getEventById(it) }

                if (event == null) {
                    uiState =
                        UiState.Error(getString(R.string.error_occurred_while_receiving_data))
                    updateUiState()
                } else {
                    uiState = UiState.Success(event)
                    updateUiState()
                }
            }

            downloadTask = executor.submit(loadList)
        }
    }

    private fun getEventById(id: Int): Event? {
        return eventId?.let { EventRepository.getEventById(id, requireContext()) }
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

    private fun initPhoneNumbers(phoneNumbers: List<String>) {
        if (phoneNumbers.isNotEmpty()) {
            binding.emailLayout.visibility = View.VISIBLE
            binding.phoneTV.text = phoneNumbers.joinToString("\n")
        } else {
            binding.emailLayout.visibility = View.GONE
        }
    }

    private fun initImage(imageResource: Int) {
        try {
            val drawable = ContextCompat.getDrawable(requireContext(), imageResource)
            if (drawable != null) {
                binding.previewIV.visibility = View.VISIBLE
                binding.previewIV.setImageDrawable(drawable)
            } else {
                binding.previewIV.visibility = View.GONE
            }
        } catch (e: Resources.NotFoundException) {
            binding.previewIV.visibility = View.GONE
        }
    }

    private fun initOrganizerUrlText(siteUrl: String) {
        if (siteUrl.isNotBlank()) {
            binding.organizerUrlTV.visibility = View.VISIBLE
            binding.organizerUrlTV.setOnClickListener {
                openLink(siteUrl)
            }
        } else {
            binding.organizerUrlTV.visibility = View.GONE
        }
    }

    private fun openLink(link: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(link))
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        downloadTask?.cancel(true)
    }

    companion object {
        const val TAG = "EventDetailsFragment"
        private const val EVENT_ID_KEY = "EventId"
        const val EVENT_MODEL_KEY = "EventModel"

        @JvmStatic
        fun newInstance(eventId: Int) =
            EventDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(EVENT_ID_KEY, eventId)
                }
            }
    }
}
