package com.example.news.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.common_compose.theme.CircularProgressIndicator
import com.example.common_compose.theme.CommonText
import com.example.common_compose.theme.HeaderText
import com.example.common_compose.theme.SimbirSoftMobileTheme
import com.example.news.models.EventShortUi
import com.example.ui.UiText
import com.example.common_view.R as commonR
import com.example.news.R as newsR

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewsScreen(
    modifier: Modifier = Modifier,
    state: NewsState,
    onEvent: (NewsEvent) -> Unit,
    sideEffect: NewsSideEffect,
    navigateToEventDetails: (String) -> Unit,
    navigateToSettings: () -> Unit,
) {
    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    HeaderText(
                        text = stringResource(id = commonR.string.news),
                        color = MaterialTheme.colorScheme.onPrimary,
                    )
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.primary),
                actions = {
                    IconButton(
                        onClick = { navigateToSettings() },
                        colors = IconButtonDefaults.iconButtonColors(
                            contentColor = MaterialTheme.colorScheme.onTertiary,
                        )
                    ) {
                        Icon(
                            painter = painterResource(id = commonR.drawable.ic_filter),
                            contentDescription = stringResource(id = commonR.string.filter),
                        )
                    }
                }
            )
        }
    ) { paddings ->
        Box(
            modifier = modifier
                .padding(paddings),
        ) {
            when {
                state.isLoading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.fillMaxSize()
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null -> {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                dimensionResource(
                                    id = newsR.dimen.events_recycler_padding
                                )
                            )
                    ) {
                        CommonText(text = state.error.asString(LocalContext.current))
                    }
                }

                state.news.isNotEmpty() -> {
                    LazyColumn(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        modifier = Modifier.padding(
                            dimensionResource(
                                id = newsR.dimen.events_recycler_padding
                            )
                        )
                    ) {
                        items(
                            items = state.news,
                            key = { it.id }
                        ) {
                            EventCard(
                                event = it,
                                onClick = navigateToEventDetails,
                            )
                            Spacer(
                                modifier = Modifier.height(
                                    dimensionResource(id = newsR.dimen.event_item_spacer)
                                )
                            )
                        }
                    }
                }
            }
        }

    }
}

@Preview(showBackground = true, locale = "ru")
@Composable
private fun SuccessNewsScreenPreview() {
    SimbirSoftMobileTheme {
        NewsScreen(
            state = NewsState(news = List(10) {
                EventShortUi(
                    id = "$it",
                    name = "Заголовок новости $it",
                    123312231L,
                    123312232L,
                    description = "Дубовская школа-интернат для детей с ограниченными возможностями здоровья стала первой в области области области области области области области области",
                    -1,
                    "",
                    "1",
                    123123123132L,
                )
            }),
            onEvent = {},
            sideEffect = NewsSideEffect.NoEffect,
            navigateToEventDetails = {},
            navigateToSettings = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, locale = "ru")
@Composable
private fun LoadingNewsScreenPreview() {
    SimbirSoftMobileTheme {
        NewsScreen(
            state = NewsState(),
            onEvent = {},
            sideEffect = NewsSideEffect.NoEffect,
            navigateToEventDetails = {},
            navigateToSettings = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}

@Preview(showBackground = true, locale = "ru")
@Composable
private fun ErrorNewsScreenPreview() {
    SimbirSoftMobileTheme {
        NewsScreen(
            state = NewsState(
                error = UiText.DynamicString("Ошибка получения данных")
            ),
            onEvent = {},
            sideEffect = NewsSideEffect.NoEffect,
            navigateToEventDetails = {},
            navigateToSettings = {},
            modifier = Modifier.fillMaxSize(),
        )
    }
}
