package com.example.event_details.screen

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.get
import androidx.work.Constraints
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import coil.ImageLoader
import coil.load
import coil.request.ImageRequest
import com.example.date.getRemainingDateInfo
import com.example.event_details.databinding.FragmentEventDetailsBinding
import com.example.event_details.di.EventDetailsComponentViewModel
import com.example.event_details.di.WorkerDeps
import com.example.event_details.models.EventLongUi
import com.example.event_details.screen.notification.NotificationWorker
import com.example.ui.MviFragment
import dagger.Lazy
import javax.inject.Inject
import com.example.common_view.R as commonR
import com.example.event_details.R as detailsR

class EventDetailsFragment :
    MviFragment<EventDetailsState, EventDetailsSideEffect, EventDetailsEvent>() {
    private var _binding: FragmentEventDetailsBinding? = null
    private val binding: FragmentEventDetailsBinding
        get() = _binding!!

    @Inject
    lateinit var workerDeps: WorkerDeps

    @Inject
    lateinit var factory: Lazy<EventDetailsViewModel.Factory>

    public override lateinit var viewModel: EventDetailsViewModel

    override fun onAttach(context: Context) {
        if (!this::factory.isInitialized) {
            ViewModelProvider(this).get<EventDetailsComponentViewModel>()
                .eventDetailsComponent.inject(this)
            viewModel = ViewModelProvider(this, factory.get()).get()
        }
        super.onAttach(context)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        childFragmentManager.setFragmentResultListener(
            HelpWithMoneyDialogFragment.REQUEST_RESULT_KEY,
            this
        ) { _, bundle ->
            val result = bundle.getInt(HelpWithMoneyDialogFragment.BUNDLE_RESULT_KEY)

            viewModel.state.value.eventDetails?.let {
                launchNotificationWorker(it, result)
            }
        }
    }

    private fun launchNotificationWorker(eventDetails: EventLongUi, moneyAmount: Int) {
        val constraints = Constraints.Builder()
            .setRequiresCharging(true)
            .build()

        val data = Data.Builder().apply {
            putString(NotificationWorker.MONEY_HELP_ID_KEY, eventDetails.id)
            putString(NotificationWorker.MONEY_HELP_EVENT_NAME_KEY, eventDetails.name)
            putInt(NotificationWorker.MONEY_HELP_AMOUNT_KEY, moneyAmount)
            putBoolean(NotificationWorker.IS_REPEATED_NOTIFICATION_KEY, false)
        }

        val request = OneTimeWorkRequestBuilder<NotificationWorker>()
            .setInputData(data.build())
            .setConstraints(constraints)
            .build()

        WorkManager.getInstance(requireContext()).enqueue(request)
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
        binding.moneyIcon.setOnClickListener {
            showHelpDialog()
        }
    }

    private fun showHelpDialog() {
        HelpWithMoneyDialogFragment().show(childFragmentManager, HelpWithMoneyDialogFragment.TAG)
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
