package com.example.profile.screen

import android.net.Uri
import com.example.dataerror.getDescription
import com.example.profile.models.FriendUI
import com.example.result.DataError
import com.example.ui.MviEvent
import com.example.ui.MviSideEffect
import com.example.ui.MviState
import com.example.ui.UiText

data class ProfileState(
    val friends: List<FriendUI> = emptyList(),
    val imageUri: Uri? = null,
    val sendNotifications: Boolean = true,
) : MviState

sealed interface ProfileEvent : MviEvent {
    sealed interface Ui : ProfileEvent {
        data class UpdateImageByUri(val newImageUri: Uri) : Ui
        data object DeleteImage : Ui
        data class UpdateSendNotificationStatus(val newStatus: Boolean) : Ui
    }

    sealed interface Internal : ProfileEvent {
        data object LoadFriends : Internal
        data class FriendsLoaded(val list: List<FriendUI>) : Internal
        data class ErrorFriendsLoaded(val error: UiText) : Internal {
            constructor(error: DataError) : this(error.getDescription())
        }

        data object LoadNotificationStatus : Internal
        data class NotificationStatusLoaded(val status: Boolean) : Internal

        data object LoadProfileImageUri : Internal
        data class ProfileImageUriLoaded(val uri: Uri) : Internal
    }
}

sealed interface ProfileSideEffect : MviSideEffect {
    data object RefreshProfileImage : ProfileSideEffect
    data class ShowErrorMessage(val error: UiText) : ProfileSideEffect {
        constructor(error: DataError) : this(error.getDescription())
    }
}
