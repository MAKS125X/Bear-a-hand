package com.example.simbirsoftmobile.presentation.screens.profile

import android.net.Uri
import com.example.simbirsoftmobile.presentation.base.MviEvent
import com.example.simbirsoftmobile.presentation.base.MviSideEffect
import com.example.simbirsoftmobile.presentation.base.MviState
import com.example.simbirsoftmobile.presentation.base.UiText
import com.example.simbirsoftmobile.presentation.models.friend.FriendUI

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
