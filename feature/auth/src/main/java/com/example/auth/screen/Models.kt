package com.example.auth.screen

import com.example.dataerror.getDescription
import com.example.result.DataError
import com.example.ui.MviEvent
import com.example.ui.MviSideEffect
import com.example.ui.MviState
import com.example.ui.UiText

data class AuthState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val error: UiText? = null,
    val email: String = "test@test.test",
    val password: String = "test@test.test",
    val isAuthClickable: Boolean = false,
) : MviState

sealed interface AuthEvent : MviEvent {
    sealed interface Ui : AuthEvent {
        data object Authenticate : Ui
        data class UpdateEmail(val value: String) : Ui
        data class UpdatePassword(val value: String) : Ui
        data object NavigateToRegistration : Ui
        data object NavigateToForgetPassword : Ui
    }

    sealed interface Internal : AuthEvent {
        data class AuthError(val error: UiText) : Internal {
            constructor(error: DataError) : this(error.getDescription())
        }

        data object AuthSuccess : Internal
    }
}

sealed interface AuthSideEffect : MviSideEffect {
    data object NavigateToContent : AuthSideEffect
    data class NavigateToRegistration(val text: UiText) : AuthSideEffect
    data class NavigateToForgetPassword(val text: UiText) : AuthSideEffect
}
