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
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.disposables.CompositeDisposable

class EventDetailsFragment : Fragment() {
    private var _binding: FragmentEventDetailsBinding? = null
    private val binding: FragmentEventDetailsBinding
        get() = _binding!!

    private val compositeDisposable = CompositeDisposable()
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
            val currentId = eventId
            if (currentId != null) {
                getEventById(currentId)
            } else {
                UiState.Error(getString(R.string.error_occurred_while_receiving_data))
                updateUiState()
            }
        }
    }

    private fun getEventById(id: Int) {
        val dispose = EventRepository
            .getEventById(id, requireContext())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe { event ->
                uiState = UiState.Success(event)
                updateUiState()
            }
        compositeDisposable.add(dispose)
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
        with(binding) {
            try {
                val drawable = ContextCompat.getDrawable(requireContext(), imageResource)
                if (drawable != null) {
                    previewIV.visibility = View.VISIBLE
                    previewIV.setImageDrawable(drawable)
                } else {
                    previewIV.visibility = View.GONE
                }
            } catch (e: Resources.NotFoundException) {
                previewIV.visibility = View.GONE
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
        compositeDisposable.dispose()
    }

    companion object {
        const val TAG = "EventDetailsFragment"
        const val EVENT_ID_KEY = "eventId"
        private const val EVENT_MODEL_KEY = "EventModel"

        @JvmStatic
        fun newInstance(eventId: Int) =
            EventDetailsFragment().apply {
                arguments = Bundle().apply {
                    putInt(EVENT_ID_KEY, eventId)
                }
            }
    }
}
