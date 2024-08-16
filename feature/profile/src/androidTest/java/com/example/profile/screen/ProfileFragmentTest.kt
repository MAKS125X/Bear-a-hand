package com.example.profile.screen

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.provider.MediaStore
import androidx.fragment.app.testing.launchFragmentInContainer
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasAction
import androidx.test.espresso.matcher.RootMatchers.isDialog
import androidx.test.espresso.matcher.ViewMatchers.isChecked
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.isNotChecked
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.espresso.matcher.ViewMatchers.withText
import androidx.test.internal.runner.junit4.AndroidJUnit4ClassRunner
import androidx.test.rule.GrantPermissionRule
import androidx.test.rule.GrantPermissionRule.grant
import com.example.profile.R
import com.example.profile.models.FriendUI
import com.example.profile.useCase.DeleteProfileImageUseCase
import com.example.profile.useCase.GetFriendListUseCase
import com.example.profile.useCase.GetProfileImageUriUseCase
import com.example.profile.useCase.GetShowNotificationSettingUseCase
import com.example.profile.useCase.SetShowNotificationSettingUseCase
import com.example.profile.useCase.UpdateProfileImageUseCase
import com.example.result.Either
import com.example.test_matchers.hasDrawable
import dagger.Lazy
import kotlinx.coroutines.flow.flowOf
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.doReturn
import org.mockito.kotlin.stub
import org.mockito.kotlin.whenever
import com.example.common_view.R as commonR

@RunWith(AndroidJUnit4ClassRunner::class)
class ProfileFragmentTest {
    private lateinit var getFriendListUseCase: GetFriendListUseCase
    private lateinit var getNotificationSettingUseCase: GetShowNotificationSettingUseCase
    private lateinit var setShowNotificationSettingUseCase: SetShowNotificationSettingUseCase
    private lateinit var getProfileImageUriUseCase: GetProfileImageUriUseCase
    private lateinit var updateProfileImageUseCase: UpdateProfileImageUseCase
    private lateinit var deleteProfileImageUseCase: DeleteProfileImageUseCase
    private lateinit var viewModel: ProfileViewModel
    private lateinit var lazyViewModelFactory: Lazy<ProfileViewModel.Factory>

    @JvmField
    @Rule
    val permissionRule: GrantPermissionRule = grant(
        Manifest.permission.POST_NOTIFICATIONS
    )

    @Before
    fun setUp() {
        getFriendListUseCase = mock()
        getNotificationSettingUseCase = mock()
        setShowNotificationSettingUseCase = mock()
        getProfileImageUriUseCase = mock()
        updateProfileImageUseCase = mock()
        deleteProfileImageUseCase = mock()
        viewModel = ProfileViewModel(
            getFriendListUseCase,
            getNotificationSettingUseCase,
            setShowNotificationSettingUseCase,
            getProfileImageUriUseCase,
            updateProfileImageUseCase,
            deleteProfileImageUseCase,
        )

        val viewModelFactory = mock(ProfileViewModel.Factory::class.java)
        whenever(viewModelFactory.create(ProfileViewModel::class.java)).thenReturn(
            viewModel
        )

        lazyViewModelFactory = Lazy { viewModelFactory }
        Intents.init()
    }

    @After
    fun tearDown() {
        Intents.release()
    }

    @Test
    fun launch_AllUseCaseWithSuccess_RecyclerViewAndSwitchInitialised() {
        mockSuccessNotificationSettingUseCase()
        mockSuccessGetFriendListUseCase()

        launchFragmentInContainer<ProfileFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestProfileFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.friendRecycler))
            .check(matches(isDisplayed()))
        onView(withId(R.id.notificationSwitch))
            .check(matches(isDisplayed()))
    }

    @Test
    fun loadFriendList_FriendListLoaded_RecyclerViewHasCorrectItemCount() {
        val recyclerViewCount = 3

        mockSuccessGetFriendListUseCase(recyclerViewCount)

        launchFragmentInContainer<ProfileFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestProfileFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.friendRecycler))
            .check(matches(isDisplayed()))
            .check(matches(com.example.test_matchers.hasItemCount(recyclerViewCount)))
    }

    @Test
    fun loadNotificationStatus_NotificationStatusLoaded_SwitchHasCorrectCheckedValue() {
        mockSuccessGetFriendListUseCase()
        mockSuccessNotificationSettingUseCase(false)

        launchFragmentInContainer<ProfileFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestProfileFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.notificationSwitch))
            .check(matches(isNotChecked()))
    }

    @Test
    fun changeNotificationStatus_NotificationStatusChangedAndPostNotificationPermissionGranted_SwitchHasCorrectCheckedValue() {
        val initialSwitchCheckValue = false
        mockSuccessGetFriendListUseCase()
        mockSuccessNotificationSettingUseCase(initialSwitchCheckValue)
        mockSuccessSetNotificationStatusUseCase()

        launchFragmentInContainer<ProfileFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestProfileFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.notificationSwitch))
            .check(matches(isNotChecked()))
            .perform(click())
            .check(matches(isChecked()))
    }

    @Test
    fun profileImageClick_EditPhotoDialogOpened() {
        mockSuccessGetFriendListUseCase()
        mockSuccessNotificationSettingUseCase()

        launchFragmentInContainer<ProfileFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestProfileFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.profileIV))
            .perform(click())
        onView(withText(R.string.pick_photo))
            .check(matches(isDisplayed()))
    }

    @Test
    fun profileImageClick_EditPhotoDialogOpened_ClickedTakePhoto_IntentSent() {
        mockSuccessGetFriendListUseCase()
        mockSuccessNotificationSettingUseCase()
        mockSuccessGetProfileImageUriUseCase()

        launchFragmentInContainer<ProfileFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestProfileFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.profileIV))
            .perform(click())
        onView(withText(R.string.take_picture))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())

        intended(hasAction(Intent(MediaStore.ACTION_IMAGE_CAPTURE).action))
    }

    @Test
    fun profileImageClick_EditPhotoDialogOpened_ClickedPickImage_IntentSent() {
        mockSuccessGetFriendListUseCase()
        mockSuccessNotificationSettingUseCase()

        launchFragmentInContainer<ProfileFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestProfileFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.profileIV))
            .perform(click())
        onView(withText(R.string.pick_photo))
            .inRoot(isDialog())
            .check(matches(isDisplayed()))
            .perform(click())

        intended(hasAction(Intent.ACTION_PICK))
    }

    @Test
    fun profileImageClick_EditPhotoDialogOpened_ClickedDeleteImage_ProfileImageDeleted() {
        mockSuccessGetFriendListUseCase()
        mockSuccessNotificationSettingUseCase()

        launchFragmentInContainer<ProfileFragment>(
            themeResId = commonR.style.Theme_SimbirSoftMobile,
            factory = TestProfileFragmentFactory(lazyViewModelFactory, viewModel)
        )

        onView(withId(R.id.profileIV))
            .perform(click())
        onView(withId(R.id.deletePictureLayout))
            .perform(click())
        onView(withId(R.id.profileIV))
            .check(matches(hasDrawable()))
            .check(matches(com.example.test_matchers.withBitmap(commonR.drawable.ic_standard_profile)))
    }

    private fun mockSuccessGetFriendListUseCase(listCount: Int = 5) {
        val friendList = List(listCount) {
            FriendUI("friend$it", "imageUrl$it")
        }

        getFriendListUseCase.stub {
            onBlocking { invoke() } doReturn flowOf(Either.Right(friendList))
        }
    }

    private fun mockSuccessNotificationSettingUseCase(value: Boolean = false) {
        getNotificationSettingUseCase.stub {
            onBlocking { invoke() } doReturn Either.Right(value)
        }
    }

    private fun mockSuccessGetProfileImageUriUseCase() {
        getProfileImageUriUseCase.stub {
            onBlocking { invoke() } doReturn Either.Right(Uri.EMPTY.toString())
        }
    }

    private fun mockSuccessSetNotificationStatusUseCase() {
        setShowNotificationSettingUseCase.stub {
            onBlocking { invoke(true) } doReturn Either.Right(Unit)
            onBlocking { invoke(false) } doReturn Either.Right(Unit)
        }
    }
}