package com.example.profile.screen

import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.example.profile.useCase.DeleteProfileImageUseCase
import com.example.profile.useCase.GetFriendListUseCase
import com.example.profile.useCase.GetProfileImageUriUseCase
import com.example.profile.useCase.GetShowNotificationSettingUseCase
import com.example.profile.useCase.SetShowNotificationSettingUseCase
import com.example.profile.useCase.UpdateProfileImageUseCase
import com.example.result.DataError
import com.example.result.processResult
import com.example.ui.MviViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject
import javax.inject.Provider

class ProfileViewModel(
    private val getFriendListUseCase: GetFriendListUseCase,
    private val getShowNotificationSettingUseCase: GetShowNotificationSettingUseCase,
    private val setShowNotificationSettingUseCase: SetShowNotificationSettingUseCase,
    private val getProfileImageUriUseCase: GetProfileImageUriUseCase,
    private val updateProfileImageUseCase: UpdateProfileImageUseCase,
    private val deleteProfileImageUseCase: DeleteProfileImageUseCase,
) : MviViewModel<ProfileState, ProfileSideEffect, ProfileEvent>(
    ProfileState()
) {
    override fun reduce(state: ProfileState, event: ProfileEvent) {
        when (event) {
            ProfileEvent.Internal.LoadFriends -> {
                loadFriends()
            }

            is ProfileEvent.Internal.FriendsLoaded -> {
                updateState(
                    state.copy(
                        friends = event.list,
                    )
                )
            }

            is ProfileEvent.Internal.ErrorFriendsLoading -> {
                updateState(
                    state.copy(
                        friends = emptyList(),
                    )
                )
                processSideEffect(ProfileSideEffect.ShowErrorMessage(event.error))
            }

            is ProfileEvent.Ui.UpdateSendNotificationStatus -> {
                sendNewNotificationStatus(event.newStatus)
            }

            ProfileEvent.Internal.LoadNotificationStatus -> {
                loadNotificationStatus()
            }

            is ProfileEvent.Internal.NotificationStatusLoaded -> {
                updateState(
                    state.copy(
                        sendNotifications = event.status,
                    )
                )
            }

            ProfileEvent.Internal.LoadProfileImageUri -> {
                getProfileImage()
            }

            is ProfileEvent.Internal.ProfileImageUriLoaded -> {
                updateState(
                    state.copy(
                        imageUri = event.uri
                    )
                )
            }

            ProfileEvent.Ui.DeleteImage -> {
                deleteProfileImage()
            }

            is ProfileEvent.Ui.UpdateImageByUri -> {
                updateImageByUri(event.newImageUri)
            }
        }
    }

    private fun updateImageByUri(newImageUri: Uri) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            processSideEffect(ProfileSideEffect.ShowErrorMessage(DataError.Unexpected()))
        }

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val result = async {
                updateProfileImageUseCase.invoke(newImageUri.toString())
            }
            result.await().processResult(
                onError = {
                    processSideEffect(ProfileSideEffect.ShowErrorMessage(it))
                },
                onSuccess = {
                    processSideEffect(ProfileSideEffect.RefreshProfileImage)
                }
            )
        }
    }

    private fun deleteProfileImage() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            processSideEffect(ProfileSideEffect.ShowErrorMessage(DataError.Unexpected()))
        }

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val result = async {
                deleteProfileImageUseCase.invoke()
            }
            result.await().processResult(
                onError = {
                    processSideEffect(ProfileSideEffect.ShowErrorMessage(it))
                },
                onSuccess = {
                    processSideEffect(ProfileSideEffect.RefreshProfileImage)
                }
            )
        }
    }

    private fun getProfileImage() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            processSideEffect(ProfileSideEffect.ShowErrorMessage(DataError.Unexpected()))
        }

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val result = async {
                getProfileImageUriUseCase.invoke()
            }
            result.await().processResult(
                onError = {
                    processSideEffect(ProfileSideEffect.ShowErrorMessage(it))
                },
                onSuccess = {
                    consumeEvent(ProfileEvent.Internal.ProfileImageUriLoaded(Uri.parse(it)))
                }
            )
        }
    }

    private fun sendNewNotificationStatus(newStatus: Boolean) {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            processSideEffect(ProfileSideEffect.ShowErrorMessage(DataError.Unexpected()))
        }

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val result = async {
                setShowNotificationSettingUseCase.invoke(newStatus)
            }
            result.await().processResult(
                onError = {
                    processSideEffect(ProfileSideEffect.ShowErrorMessage(it))
                },
                onSuccess = {
                    consumeEvent(ProfileEvent.Internal.LoadNotificationStatus)
                }
            )
        }
    }

    private fun loadNotificationStatus() {
        val exceptionHandler = CoroutineExceptionHandler { _, _ ->
            processSideEffect(ProfileSideEffect.ShowErrorMessage(DataError.Unexpected()))
        }

        viewModelScope.launch(Dispatchers.IO + exceptionHandler) {
            val result = async {
                getShowNotificationSettingUseCase.invoke()
            }
            result.await().processResult(
                onError = {
                    processSideEffect(ProfileSideEffect.ShowErrorMessage(it))
                },
                onSuccess = {
                    consumeEvent(ProfileEvent.Internal.NotificationStatusLoaded(it))
                }
            )
        }
    }

    private fun loadFriends() {
        getFriendListUseCase
            .invoke()
            .onEach { either ->
                either.processResult(
                    onError = {
                        consumeEvent(ProfileEvent.Internal.ErrorFriendsLoading(it))

                    },
                    onSuccess = {
                        consumeEvent(ProfileEvent.Internal.FriendsLoaded(it))
                    }
                )
            }
            .launchIn(viewModelScope)
    }

    class Factory @Inject constructor(
        private val getShowNotificationSettingUseCase: Provider<GetShowNotificationSettingUseCase>,
        private val setShowNotificationSettingUseCase: Provider<SetShowNotificationSettingUseCase>,
        private val getProfileImageUriUseCase: Provider<GetProfileImageUriUseCase>,
        private val updateProfileImageUseCase: Provider<UpdateProfileImageUseCase>,
        private val deleteProfileImageUseCase: Provider<DeleteProfileImageUseCase>,
    ) : ViewModelProvider.Factory {
        @Suppress("UNCHECKED_CAST")
        override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
            require(modelClass == ProfileViewModel::class.java)
            return ProfileViewModel(
                GetFriendListUseCase(),
                getShowNotificationSettingUseCase.get(),
                setShowNotificationSettingUseCase.get(),
                getProfileImageUriUseCase.get(),
                updateProfileImageUseCase.get(),
                deleteProfileImageUseCase.get(),
            ) as T
        }
    }
}
