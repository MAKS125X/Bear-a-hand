package com.example.simbirsoftmobile.domain.core

sealed class NetworkError {
    class Unexpected(val error: String?) : NetworkError()

    class InvalidParameters(val error: String?) : NetworkError()

    class Api(val error: String?) : NetworkError()

    data object Connection : NetworkError()

    data object Timeout : NetworkError()
}
