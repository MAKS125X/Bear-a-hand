package com.example.common_compose.theme

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
fun HorizontalDivider(modifier: Modifier = Modifier) {
    androidx.compose.material3.HorizontalDivider(
        modifier = modifier,
        color = DividerColor,
    )
}

@Composable
fun VerticalDivider(modifier: Modifier = Modifier) {
    androidx.compose.material3.VerticalDivider(
        modifier = modifier,
        color = DividerColor,
    )
}
