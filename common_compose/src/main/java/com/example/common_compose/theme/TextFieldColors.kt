package com.example.common_compose.theme

import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

val TextFieldColors
    @Composable
    get() = TextFieldDefaults.colors(
        focusedContainerColor = Color.Transparent,
        unfocusedContainerColor = Color.Transparent,
        disabledContainerColor = Color.Transparent,
        errorContainerColor = Color.Transparent,
        focusedTextColor = HelperTextColor,
        unfocusedTextColor = HelperTextColor,
        disabledTextColor = HelperTextColor,
        errorTextColor = HelperTextColor,
    )
