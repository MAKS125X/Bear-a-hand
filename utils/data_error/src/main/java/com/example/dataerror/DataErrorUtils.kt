package com.example.dataerror

import android.content.Context
import com.example.result.DataError
import com.example.ui.UiText

fun DataError.getDescription(context: Context): String {
    return when (this) {
        is DataError.Api -> this.error
            ?: context.getString(R.string.error_occurred_while_receiving_data)

        is DataError.InvalidParameters -> context.getString(R.string.unexpected_error)
        DataError.Timeout -> context.getString(R.string.timeout_error)
        is DataError.Unexpected -> context.getString(R.string.unexpected_error)
        is DataError.Connection -> context.getString(R.string.connection_error)
        is DataError.NetworkBlock -> context.getString(R.string.unexpected_error)
    }
}

fun DataError.getDescription(): UiText {
    return when (this) {
        is DataError.Api -> this.error?.let { UiText.DynamicString(it) }
            ?: UiText.StringResource(R.string.error_occurred_while_receiving_data)

        is DataError.InvalidParameters -> UiText.StringResource(R.string.unexpected_error)
        DataError.Timeout -> UiText.StringResource(R.string.timeout_error)
        is DataError.Unexpected -> UiText.StringResource(R.string.unexpected_error)
        is DataError.Connection -> UiText.StringResource(R.string.connection_error)
        is DataError.NetworkBlock -> UiText.StringResource(R.string.unexpected_error)
    }
}
