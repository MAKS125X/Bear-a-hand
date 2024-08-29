package com.example.ui

import android.content.Context
import androidx.annotation.StringRes

sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any,
    ) : UiText()

    fun asString(context: Context): String =
        when (this) {
            is DynamicString -> value
            is StringResource -> context.getString(resId, *args)
        }

    override fun equals(other: Any?): Boolean {
        return when {
            this is DynamicString && other is DynamicString
                    && this.value == other.value -> true

            this is StringResource && other is StringResource
                    && this.resId == other.resId && this.args.contentEquals(other.args) -> true

            else -> false
        }
    }

    override fun hashCode(): Int {
        return when (this) {
            is DynamicString -> value.hashCode()

            is StringResource -> resId.hashCode()
        }
    }
}
