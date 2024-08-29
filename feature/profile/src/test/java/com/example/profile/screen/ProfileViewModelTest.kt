package com.example.profile.screen

import android.net.Uri
import com.example.dataerror.getDescription
import com.example.profile.models.FriendUI
import com.example.profile.useCase.DeleteProfileImageUseCase
import com.example.profile.useCase.GetFriendListUseCase
import com.example.profile.useCase.GetProfileImageUriUseCase
import com.example.profile.useCase.GetShowNotificationSettingUseCase
import com.example.profile.useCase.SetShowNotificationSettingUseCase
import com.example.profile.useCase.UpdateProfileImageUseCase
import com.example.result.DataError
import com.example.result.Either
import com.example.test_rules.MainDispatcherRule
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.mock
import org.mockito.kotlin.whenever
import org.robolectric.RobolectricTestRunner


@RunWith(RobolectricTestRunner::class)

@OptIn(ExperimentalCoroutinesApi::class)
class ProfileViewModelTest {
    @get:Rule
    val mainDispatcherRule = MainDispatcherRule(StandardTestDispatcher())

    private lateinit var viewModel: ProfileViewModel
    private lateinit var getFriendListUseCase: GetFriendListUseCase
    private lateinit var getNotificationSettingUseCase: GetShowNotificationSettingUseCase
    private lateinit var setShowNotificationSettingUseCase: SetShowNotificationSettingUseCase
    private lateinit var getProfileImageUriUseCase: GetProfileImageUriUseCase
    private lateinit var updateProfileImageUseCase: UpdateProfileImageUseCase
    private lateinit var deleteProfileImageUseCase: DeleteProfileImageUseCase

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
            StandardTestDispatcher()
        )
    }

    @Test
    fun friendsLoaded_FriendListLoaded_HoldsStateWithList() = runTest {
        val friendList = List(3) {
            FriendUI("friend$it", "imageUrl$it")
        }

        viewModel.consumeEvent(ProfileEvent.Internal.FriendsLoaded(friendList))

        advanceUntilIdle()

        assertEquals(
            friendList,
            viewModel.state.value.friends
        )
    }

    @Test
    fun friendsLoaded_ErrorLoaded_ProcessShowErrorMessageEffectAndHoldStateWithEmptyList() =
        runTest {
            val error = DataError.Unexpected("Test error")
            val deferredEffect = async {
                viewModel.effects.first()
            }
            val deferredState = async {
                viewModel.state.first()
            }

            viewModel.consumeEvent(ProfileEvent.Internal.ErrorFriendsLoaded(error))

            advanceUntilIdle()

            assertEquals(
                emptyList<FriendUI>(),
                deferredState.await().friends,
            )
            assertEquals(
                ProfileSideEffect.ShowErrorMessage(error),
                deferredEffect.await(),
            )
        }

    @Test
    fun loadFriends_FriendListLoaded_HoldsStateWithList() = runTest {
        val friendList = List(3) {
            FriendUI("friend$it", "imageUrl$it")
        }

        whenever(getFriendListUseCase.invoke())
            .thenReturn(flowOf(Either.Right(friendList)))

        viewModel.consumeEvent(ProfileEvent.Internal.LoadFriends)

        advanceUntilIdle()

        assertEquals(
            friendList,
            viewModel.state.value.friends
        )
    }

    @Test
    fun loadFriends_ErrorLoaded_ProcessShowErrorMessageEffect() = runTest {
        val error = DataError.Unexpected("Test error")
        val deferredEffect = async {
            viewModel.effects.first()
        }
        val deferredState = async {
            viewModel.state.first()
        }

        whenever(getFriendListUseCase.invoke())
            .thenReturn(flowOf(Either.Left(error)))

        viewModel.consumeEvent(ProfileEvent.Internal.LoadFriends)

        advanceUntilIdle()

        assertEquals(
            emptyList<FriendUI>(),
            deferredState.await().friends,
        )
        assertEquals(
            ProfileSideEffect.ShowErrorMessage(error.getDescription()),
            deferredEffect.await(),
        )
    }

    @Test
    fun loadImageUri_ImageLoaded_HoldStateWithNewImageUri() = runTest {
        val uriString = "TestUri"
        val uri = Uri.parse(uriString)

        whenever(getProfileImageUriUseCase.invoke())
            .thenReturn(Either.Right(uriString))

        viewModel.consumeEvent(ProfileEvent.Internal.LoadProfileImageUri)

        advanceUntilIdle()

        assertEquals(
            uri,
            viewModel.state.value.imageUri,
        )
    }

    @Test
    fun loadImageUri_ErrorLoaded_ProcessShowErrorMessageEffect() = runTest {
        val error = DataError.Unexpected("TestError")
        val deferredEffect = async {
            viewModel.effects.first()
        }

        whenever(getProfileImageUriUseCase.invoke())
            .thenReturn(Either.Left(error))

        viewModel.consumeEvent(ProfileEvent.Internal.LoadProfileImageUri)

        advanceUntilIdle()

        assertEquals(
            ProfileSideEffect.ShowErrorMessage(error),
            deferredEffect.await(),
        )
    }

    @Test
    fun updateImageByUri_ImageUpdated_ProcessRefreshProfileImageEffect() = runTest {
        val uriString = "TestUri"
        val uri = Uri.parse(uriString)

        val deferredEffect = async {
            viewModel.effects.first()
        }

        whenever(updateProfileImageUseCase.invoke(uriString))
            .thenReturn(Either.Right(Unit))

        viewModel.consumeEvent(ProfileEvent.Ui.UpdateImageByUri(uri))
        advanceUntilIdle()

        assertEquals(
            ProfileSideEffect.RefreshProfileImage,
            deferredEffect.await()
        )
    }

    @Test
    fun updateImageByUri_ErrorLoaded_ProcessShowErrorMessageEffect() = runTest {
        val uriString = "TestUri"
        val uri = Uri.parse(uriString)
        val error = DataError.Unexpected("TestError")
        val deferredEffect = async {
            viewModel.effects.first()
        }

        whenever(updateProfileImageUseCase.invoke(uriString))
            .thenReturn(Either.Left(error))

        viewModel.consumeEvent(ProfileEvent.Ui.UpdateImageByUri(uri))

        advanceUntilIdle()

        assertEquals(
            ProfileSideEffect.ShowErrorMessage(error),
            deferredEffect.await()
        )
    }

    @Test
    fun deleteImage_ImageDeletedSuccessfully_ProcessRefreshImageEffect() = runTest {
        val deferredEffect = async {
            viewModel.effects.first()
        }

        whenever(deleteProfileImageUseCase.invoke())
            .thenReturn(Either.Right(Unit))

        viewModel.consumeEvent(ProfileEvent.Ui.DeleteImage)

        advanceUntilIdle()

        assertEquals(
            ProfileSideEffect.RefreshProfileImage,
            deferredEffect.await(),
        )
    }

    @Test
    fun deleteImage_ErrorReturned_ProcessShowErrorMessageEffect() = runTest {
        val error = DataError.Unexpected("Test error")
        val deferredEffect = async {
            viewModel.effects.first()
        }

        whenever(deleteProfileImageUseCase.invoke())
            .thenReturn(Either.Left(error))

        viewModel.consumeEvent(ProfileEvent.Ui.DeleteImage)

        advanceUntilIdle()

        assertEquals(
            ProfileSideEffect.ShowErrorMessage(error),
            deferredEffect.await(),
        )
    }

    @Test
    fun getNotificationStatus_StatusLoaded_HoldStateWithStatus() = runTest {
        val loadedStatus = false

        whenever(getNotificationSettingUseCase.invoke())
            .thenReturn(Either.Right(loadedStatus))

        viewModel.consumeEvent(ProfileEvent.Internal.LoadNotificationStatus)

        advanceUntilIdle()

        assertEquals(
            loadedStatus,
            viewModel.state.value.sendNotifications,
        )
    }

    @Test
    fun getNotificationStatus_ErrorLoaded_ProcessShowErrorMessageEffect() = runTest {
        val error = DataError.Unexpected("Test error")

        val deferredEffect = async {
            viewModel.effects.first()
        }

        whenever(getNotificationSettingUseCase.invoke())
            .thenReturn(Either.Left(error))

        viewModel.consumeEvent(ProfileEvent.Internal.LoadNotificationStatus)

        advanceUntilIdle()

        assertEquals(
            ProfileSideEffect.ShowErrorMessage(error),
            deferredEffect.await(),
        )
    }

    @Test
    fun notificationStatusLoaded_FalseLoaded_HoldStateWithLoadedStatus() = runTest {
        val loadedStatus = false

        viewModel.consumeEvent(ProfileEvent.Internal.NotificationStatusLoaded(loadedStatus))

        advanceUntilIdle()


        assertEquals(
            loadedStatus,
            viewModel.state.value.sendNotifications,
        )
    }

    @Test
    fun notificationStatusLoaded_TrueLoaded_HoldStateWithLoadedStatus() = runTest {
        val loadedStatus = true

        viewModel.consumeEvent(ProfileEvent.Internal.NotificationStatusLoaded(loadedStatus))

        advanceUntilIdle()

        assertEquals(
            loadedStatus,
            viewModel.state.value.sendNotifications,
        )
    }

    @Test
    fun updateNotificationStatus_UpdatedSuccessfully_HoldStateWithUpdatedStatus() = runTest {
        val newStatus = false

        whenever(setShowNotificationSettingUseCase.invoke(newStatus))
            .thenReturn(Either.Right(Unit))

        whenever(getNotificationSettingUseCase.invoke())
            .thenReturn(Either.Right(newStatus))

        viewModel.consumeEvent(ProfileEvent.Ui.UpdateSendNotificationStatus(newStatus))

        advanceUntilIdle()

        assertEquals(
            newStatus,
            viewModel.state.value.sendNotifications,
        )
    }

    @Test
    fun updateNotificationStatus_FalseLoaded_ProcessShowErrorMessageEffect() = runTest {
        val newStatus = false
        val error = DataError.Unexpected("Test error")

        val deferredEffect = async {
            viewModel.effects.first()
        }

        whenever(setShowNotificationSettingUseCase.invoke(newStatus))
            .thenReturn(Either.Left(error))

        viewModel.consumeEvent(ProfileEvent.Ui.UpdateSendNotificationStatus(newStatus))

        advanceUntilIdle()

        assertEquals(
            ProfileSideEffect.ShowErrorMessage(error),
            deferredEffect.await(),
        )
    }
}
