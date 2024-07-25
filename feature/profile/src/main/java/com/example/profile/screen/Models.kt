package com.example.profile.screen

import android.net.Uri
import com.example.profile.models.FriendUI
import com.example.ui.MviEvent
import com.example.ui.MviSideEffect
import com.example.ui.MviState
import com.example.ui.UiText

data class ProfileState(
    val friends: List<FriendUI> = emptyList(),
    val error: UiText? = null,
    val image: Uri? = null,
) : MviState

sealed interface ProfileEvent : MviEvent {
    sealed interface Ui : ProfileEvent {
        data class UpdateImage(val uri: Uri?) : Ui
        data object DeletePicture : Ui
    }

    sealed interface Internal : ProfileEvent {
        data object LoadFriends : Internal
        data class FriendsLoaded(val list: List<FriendUI>) : Internal
    }
}

sealed interface ProfileSideEffect : MviSideEffect {
    data object DeletePicture : ProfileSideEffect
}
