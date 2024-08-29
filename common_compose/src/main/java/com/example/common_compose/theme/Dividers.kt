package com.example.common_compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.HorizontalDivider as ComposeHorizontalDivider
import androidx.compose.material3.VerticalDivider as ComposeVerticalDivider

@Composable
fun HorizontalDivider(modifier: Modifier = Modifier) {
    ComposeHorizontalDivider(
        modifier = modifier,
        color = DividerColor,
    )
}

@Composable
fun VerticalDivider(modifier: Modifier = Modifier) {
    ComposeVerticalDivider(
        modifier = modifier,
        color = DividerColor,
    )
}
