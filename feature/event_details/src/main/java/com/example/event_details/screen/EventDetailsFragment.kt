package com.example.event_details.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import com.example.date.getRemainingDateInfo
import com.example.event_details.databinding.FragmentEventDetailsBinding
import com.example.event_details.di.EventDetailsComponentViewModel
import com.example.ui.MviFragment
import dagger.Lazy
import javax.inject.Inject
import com.example.common.R as commonR
import com.example.event_details.R as detailsR

class EventDetailsFragment :
    MviFragment<EventDetailsState, EventDetailsSideEffect, EventDetailsEvent>() {
    private var _binding: FragmentEventDetailsBinding? = null
    private val binding: FragmentEventDetailsBinding
        get() = _binding!!

    @Inject
    lateinit var factory: Lazy<EventDetailsViewModel.Factory>

    override val viewModel: EventDetailsViewModel by viewModels {
        factory.get()
    }

    override fun onAttach(context: Context) {
        ViewModelProvider(this).get<EventDetailsComponentViewModel>()
            .eventDetailsComponent.inject(this)
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEventDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(
        view: View,
        savedInstanceState: Bundle?,
    ) {
        super.onViewCreated(view, savedInstanceState)
        binding.toolbar.setNavigationOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    override fun renderState(state: EventDetailsState) {
        with(binding) {
            progressIndicator.isVisible = state.isLoading

            errorTV.isVisible = state.error != null
            state.error?.let {
                errorTV.text = it.asString(requireContext())
            }

            eventDetailsLayout.isVisible = state.eventDetails != null
            state.eventDetails?.let { event ->
                titleTV.text = event.name
                organizerNameTV.text = event.organization
                addressTV.text = event.address

                remainDateTV.text =
                    getRemainingDateInfo(
                        event.startDate,
                        event.endDate,
                        requireContext(),
                    )

                initEmailSection(event.email)
                initPhoneNumbers(event.phone)
                initImage(event.photo)

                descriptionTV.text = event.description
                initOrganizerUrlText(event.url)

                toolbar.setOnMenuItemClickListener {
                    when (it.itemId) {
                        detailsR.id.share_event -> {
                            val shareIntent =
                                Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, event.name)
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
        val subject = getString(detailsR.string.charitable_assistance)
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
                .placeholder(commonR.drawable.loading_animation)
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
                placeholder(commonR.drawable.loading_animation)
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

        @JvmStatic
        fun newInstance(eventId: String) =
            EventDetailsFragment().apply {
                arguments = Bundle().apply {
                    putString(EVENT_ID_KEY, eventId)
                }
            }
    }
}
