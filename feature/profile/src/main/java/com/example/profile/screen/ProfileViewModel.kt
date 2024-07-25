package com.example.profile.screen

import androidx.lifecycle.viewModelScope
import com.example.profile.models.FriendUI
import com.example.ui.MviViewModel
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class ProfileViewModel : MviViewModel<ProfileState, ProfileSideEffect, ProfileEvent>(
    ProfileState()
) {
    init {
        consumeEvent(ProfileEvent.Internal.LoadFriends)
    }

    override fun reduce(state: ProfileState, event: ProfileEvent) {
        when (event) {
            is ProfileEvent.Internal.FriendsLoaded -> {
                updateState(
                    state.copy(
                        friends = event.list,
                    )
                )
            }

            ProfileEvent.Ui.DeletePicture -> {
                updateState(
                    state.copy(
                        image = null
                    )
                )
            }

            is ProfileEvent.Ui.UpdateImage -> {
                updateState(
                    state.copy(
                        image = event.uri
                    )
                )
            }

            ProfileEvent.Internal.LoadFriends -> {
                loadFriends()
            }
        }
    }

    private fun loadFriends() {
        flowOf(
            listOf(
                FriendUI(
                    "Дмитрий Валерьевич",
                    "https://wallpaperforu.com/wp-content/uploads/2023/11/Leonardo-DiCaprio-Hd-Wallpapers-For-Pc.jpg",
                ),
                FriendUI(
                    "Евгений Александров",
                    "https://i.ytimg.com/vi/70jYjONsXTU/maxresdefault.jpg",
                ),
                FriendUI(
                    "Виктор Кузнецов",
                    "https://i.pinimg.com/originals/74/8c/ea/748ceabaaf1c09d7d00ce1813681343c.jpg",
                ),
            )
        )
            .onEach { consumeEvent(ProfileEvent.Internal.FriendsLoaded(it)) }
            .launchIn(viewModelScope)
    }
}
