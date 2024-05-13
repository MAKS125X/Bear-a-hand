package com.example.simbirsoftmobile.domain.core

sealed class DataError {
    class Unexpected(val error: String?) : DataError()

    class InvalidParameters(val error: String?) : DataError()

    class Api(val error: String?) : DataError()

    data object Connection : DataError()

    data object Timeout : DataError()

    data object NetworkBlock : DataError()
}
