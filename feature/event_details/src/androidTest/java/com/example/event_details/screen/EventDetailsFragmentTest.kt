package com.example.event_details.screen

import android.app.Activity
import android.app.Instrumentation
import android.content.Intent
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.lifecycle.SavedStateHandle
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.Intents.intending
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.platform.app.InstrumentationRegistry
import com.example.core.models.event.EventModel
import com.example.date.getRemainingDateInfo
import com.example.event_details.R
import com.example.event_details.usecase.GetEventDetailsUseCase
import com.example.result.DataError
import com.example.result.Either
import dagger.Lazy
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.doAnswer
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import org.mockito.kotlin.whenever
import com.example.common_view.R as commonR


@RunWith(AndroidJUnit4ClassRunner::class)
class EventDetailsFragmentTest {

    private lateinit var viewModel: EventDetailsViewModel
    private lateinit var lazyViewModelFactory: Lazy<EventDetailsViewModel.Factory>
    private lateinit var getEventDetailsUseCase: GetEventDetailsUseCase

    @Before
    fun setUp() {
        getEventDetailsUseCase = mock()
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun launch_EventDetailsGotWithSuccess_ScreenIsInitialised() {
        val event = getStubEventModel()
        mockSuccessGetEventDetailsUseCase(event)
        initViewModel()

        launchFragmentInContainer<EventDetailsFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestEventDetailsFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.titleTV))
            .check(matches(withText(event.name)))
        onView(withId(R.id.remainDateTV))
            .check(
                matches(
                    withText(
                        getRemainingDateInfo(
                            event.startDate,
                            event.endDate,
                            InstrumentationRegistry.getInstrumentation().targetContext,
                        )
                    )
                )
            )
        onView(withId(R.id.organizerNameTV))
            .check(matches(withText(event.organization)))
        onView(withId(R.id.addressTV))
            .check(matches(withText(event.address)))
        onView(withId(R.id.phoneTV))
            .check(matches(withText(event.phone)))
        onView(withId(R.id.descriptionTV))
            .check(matches(withText(event.description)))
    }

    @Test
    fun clickSendEmailTV_IntentSent() {
        val event = getStubEventModel()
        mockSuccessGetEventDetailsUseCase(event)
        initViewModel()
        intending(hasAction(Intent.ACTION_SENDTO)).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_OK,
                null,
            )
        )

        launchFragmentInContainer<EventDetailsFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestEventDetailsFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.sendEmailTV))
            .perform(click())

        intended(hasAction(Intent.ACTION_SENDTO))
    }

    @Test
    fun clickShareButton_IntentSent() {
        val event = getStubEventModel()
        mockSuccessGetEventDetailsUseCase(event)
        initViewModel()
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_OK,
                null,
            )
        )

        launchFragmentInContainer<EventDetailsFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestEventDetailsFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.share_event))
            .perform(click())


        intended(hasAction(Intent.ACTION_CHOOSER))
    }

    @Test
    fun clickOrganizerUrlTV_IntentSent() {
        val event = getStubEventModel()
        mockSuccessGetEventDetailsUseCase(event)
        initViewModel()
        intending(hasAction(Intent.ACTION_VIEW)).respondWith(
            Instrumentation.ActivityResult(
                Activity.RESULT_OK,
                null,
            )
        )

        launchFragmentInContainer<EventDetailsFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestEventDetailsFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.organizerUrlTV))
            .perform(click())

        intended(hasAction(Intent.ACTION_VIEW))
    }

    @Test
    fun launch_EventDetailsGotWithError_ScreenIsInitialised() {
        val error = "TestError"
        mockErrorGetEventDetailsUseCase(error)

        initViewModel()

        launchFragmentInContainer<EventDetailsFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestEventDetailsFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.errorTV))
            .check(matches(withText(error)))
    }

    @Test
    fun launch_ConsumeLongEventLoading_ProgressIndicatorIsDisplayed() {
        mockLongLoadingGetEventDetailsUseCase(getStubEventModel())

        initViewModel()

        launchFragmentInContainer<EventDetailsFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestEventDetailsFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.progressIndicator))
            .check(matches(isDisplayed()))
    }

    @Test
    fun launch_HelpWithMoneyClick_HelpWithMoneyDialogOpened() {
        mockSuccessGetEventDetailsUseCase(getStubEventModel())

        initViewModel()

        launchFragmentInContainer<EventDetailsFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestEventDetailsFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.moneyIcon))
            .perform(click())

        onView(withId(R.id.donateEditText))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
    }

    private fun initViewModel() {
        viewModel = EventDetailsViewModel(
            getEventDetailsUseCase,
            SavedStateHandle(mapOf(EventDetailsFragment.EVENT_ID_KEY to EVENT_ID)),
        )
        val viewModelFactory = mock(EventDetailsViewModel.Factory::class.java)
        whenever(viewModelFactory.create(EventDetailsViewModel::class.java)).thenReturn(
            viewModel
        )
        lazyViewModelFactory = Lazy { viewModelFactory }
    }

    private fun mockSuccessGetEventDetailsUseCase(eventModel: EventModel) {
        getEventDetailsUseCase.stub {
            onBlocking { invoke(EVENT_ID) } doReturn flowOf(Either.Right(eventModel))
        }
    }

    private fun mockLongLoadingGetEventDetailsUseCase(eventModel: EventModel) {
        getEventDetailsUseCase.stub {
            onBlocking { invoke(EVENT_ID) } doAnswer {
                flow {
                    delay(10000)
                    Either.Right(eventModel)
                }
            }
        }
    }

    private fun mockErrorGetEventDetailsUseCase(error: String) {
        getEventDetailsUseCase.stub {
            onBlocking { invoke(EVENT_ID) } doReturn flowOf(Either.Left(DataError.Api(error)))
        }
    }

    private fun getStubEventModel() = EventModel(
        "id",
        "name",
        0L,
        2L,
        "description",
        3,
        "photo",
        "categoryId",
        1L,
        "phone",
        "address",
        "email",
        "organization",
        "http://www.google.com",
        false,
    )

    companion object {
        const val EVENT_ID = "EVENT_ID"
    }
}
