package com.example.result

sealed class DataError {
    class Unexpected(val error: String?) : DataError() {
        constructor() : this(null)
    }

    class InvalidParameters(val error: String?) : DataError() {
        constructor() : this(null)
    }

    class Api(val error: String?) : DataError() {
        constructor() : this(null)
    }

    data object Connection : DataError()

    data object Timeout : DataError()

    data object NetworkBlock : DataError()
}
