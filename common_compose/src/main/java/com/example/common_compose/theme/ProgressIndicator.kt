package com.example.common_compose.theme

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.dimensionResource
import com.example.common_view.R

@Composable
fun CircularProgressIndicator(
    modifier: Modifier = Modifier,
) {
    CircularProgressIndicator(
        modifier = modifier.size(
            dimensionResource(id = R.dimen.progressIndicatorSize)
        ),
        color = MaterialTheme.colorScheme.tertiary
    )
}
