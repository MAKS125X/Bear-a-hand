package com.example.simbirsoftmobile.presentation.screens.utils

sealed class UiState<out T> {
    data object Loading : UiState<Nothing>()

    data class Error(val message: String) : UiState<Nothing>()

    data class Success<T>(val data: T) : UiState<T>()

    data object Idle : UiState<Nothing>()
}
