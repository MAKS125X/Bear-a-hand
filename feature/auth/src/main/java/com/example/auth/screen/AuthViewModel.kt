package com.example.auth.screen

import androidx.lifecycle.viewModelScope
import com.example.auth.R
import com.example.result.DataError
import com.example.result.Either
import com.example.result.processResult
import com.example.ui.MviViewModel
import com.example.ui.UiText
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach

class AuthViewModel : MviViewModel<AuthState, AuthSideEffect, AuthEvent>(AuthState()) {
    override fun reduce(state: AuthState, event: AuthEvent) {
        when (event) {
            is AuthEvent.Internal.AuthError -> {
                updateState(
                    state.copy(
                        isLoading = false,
                        error = event.error,
                    )
                )
            }

            is AuthEvent.Ui.UpdateEmail -> {
                updateState(
                    state.copy(
                        email = event.value,
                        isLoading = false,
                        error = null,
                        isAuthClickable = event.value.length >= 6 && state.password.length >= 6
                    )
                )
            }

            is AuthEvent.Ui.UpdatePassword -> {
                updateState(
                    state.copy(
                        password = event.value,
                        isLoading = false,
                        error = null,
                        isAuthClickable = event.value.length >= 6 && state.email.length >= 6
                    )
                )
            }

            is AuthEvent.Ui.Authenticate -> {
                updateState(
                    state.copy(
                        isLoading = true,
                    )
                )
                authenticate(state.email, state.password)
            }

            AuthEvent.Internal.AuthSuccess -> {
                updateState(
                    state.copy(
                        isLoading = false,
                    )
                )
                processSideEffect(
                    AuthSideEffect.NavigateToContent
                )
            }

            AuthEvent.Ui.NavigateToForgetPassword -> {
                processSideEffect(
                    AuthSideEffect.NavigateToForgetPassword(UiText.StringResource(R.string.forget_password))
                )
            }

            AuthEvent.Ui.NavigateToRegistration -> {
                processSideEffect(
                    AuthSideEffect.NavigateToRegistration(UiText.StringResource(R.string.registration))
                )
            }

            AuthEvent.Ui.ChangePasswordVisibility -> {
                updateState(
                    state.copy(
                        showPassword = !state.showPassword,
                    )
                )
            }
        }
    }

    private fun authenticate(email: String, password: String) {
        flowOf<Either<DataError, Unit>>(Either.Right(Unit))
            .onEach { response ->
                response.processResult(
                    onError = {
                        consumeEvent(AuthEvent.Internal.AuthError(it))
                    },
                    onSuccess = {
                        consumeEvent(AuthEvent.Internal.AuthSuccess)
                    }
                )
            }
            .launchIn(viewModelScope)
    }
}
