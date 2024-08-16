package com.example.news.screen

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollToNode
import com.example.common_compose.theme.CircularProgressIndicatorTag
import com.example.common_compose.theme.SimbirSoftMobileTheme
import com.example.date.getRemainingDateInfo
import com.example.news.models.EventShortUi
import com.example.ui.UiText
import org.junit.Rule
import org.junit.Test
import com.example.common_view.R as commonR

class NewsScreenTest {
    @get:Rule
    val composeTestRule = createAndroidComposeRule<ComponentActivity>()

    @Test
    fun successfulStateLoaded_AppbarIsDisplayed() {
        composeTestRule.apply {
            setContent {
                NewsScreenWithStubState(getSuccessfulStateStub())
            }
            onNodeWithText(composeTestRule.activity.getString(commonR.string.news))
                .assertExists()
                .assertIsDisplayed()
        }
    }

    @Test
    fun errorStateLoaded_AppbarIsDisplayed() {
        composeTestRule.apply {
            setContent {
                NewsScreenWithStubState(ERROR_STATE_STUB)
            }
            onNodeWithText(composeTestRule.activity.getString(commonR.string.news))
                .assertExists()
                .assertIsDisplayed()
        }
    }

    @Test
    fun loadingState_AppbarIsDisplayed() {
        composeTestRule.apply {
            setContent {
                NewsScreenWithStubState(LOADING_STATE_STUB)
            }
            onNodeWithText(composeTestRule.activity.getString(commonR.string.news))
                .assertExists()
                .assertIsDisplayed()
        }
    }

    @Test
    fun successfulStateLoaded_FilterButtonIsClicked() {
        var isClicked = false

        composeTestRule.setContent {
            SimbirSoftMobileTheme {
                NewsScreen(
                    state = getSuccessfulStateStub(),
                    onEvent = {},
                    sideEffect = NewsSideEffect.NoEffect,
                    navigateToEventDetails = {},
                    navigateToSettings = { isClicked = true },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(commonR.string.filter))
            .assertExists()
            .assertIsDisplayed()
            .performClick()

        assert(isClicked)
    }

    @Test
    fun errorStateLoaded_FilterButtonIsClicked() {
        var isClicked = false

        composeTestRule.setContent {
            SimbirSoftMobileTheme {
                NewsScreen(
                    state = ERROR_STATE_STUB,
                    onEvent = {},
                    sideEffect = NewsSideEffect.NoEffect,
                    navigateToEventDetails = {},
                    navigateToSettings = { isClicked = true },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(commonR.string.filter))
            .assertExists()
            .assertIsDisplayed()
            .performClick()

        assert(isClicked)
    }

    @Test
    fun loadingState_FilterButtonIsClicked() {
        var isClicked = false

        composeTestRule.setContent {
            SimbirSoftMobileTheme {
                NewsScreen(
                    state = LOADING_STATE_STUB,
                    onEvent = {},
                    sideEffect = NewsSideEffect.NoEffect,
                    navigateToEventDetails = {},
                    navigateToSettings = { isClicked = true },
                )
            }
        }

        composeTestRule.onNodeWithContentDescription(composeTestRule.activity.getString(commonR.string.filter))
            .assertExists()
            .assertIsDisplayed()
            .performClick()

        assert(isClicked)
    }

    @Test
    fun loadingState_ProgressBarIsDisplayed() {
        composeTestRule.setContent {
            NewsScreenWithStubState(LOADING_STATE_STUB)
        }

        composeTestRule.onNodeWithTag(CircularProgressIndicatorTag)
            .assertExists()
            .assertIsDisplayed()
    }

    @Test
    fun errorStateLoaded_ErrorIsDisplayed() {
        composeTestRule.apply {
            setContent {
                NewsScreenWithStubState(ERROR_STATE_STUB)
            }

            onNodeWithText(ERROR_STUB.asString(composeTestRule.activity))
                .assertExists()
                .assertIsDisplayed()
        }
    }

    @Test
    fun successStateLoaded_ListIsDisplayedWithCorrectSize() {
        val newsListSize = 10

        composeTestRule.apply {
            setContent {
                NewsScreenWithStubState(getSuccessfulStateStub(newsListSize))
            }

            repeat(newsListSize) {
                onNodeWithTag(NEWS_LIST_TEST_TAG)
                    .performScrollToNode(hasText(getTitleStub(it.toString())))
                    .assertIsDisplayed()
            }
        }
    }

    @Test
    fun eventCardIsDisplayed_AllComponentsIsDisplayedCorrectly() {
        val event = getEventStub("123")

        composeTestRule.apply {
            setContent {
                SimbirSoftMobileTheme {
                    EventCard(
                        event = event,
                        onClick = {},
                    )
                }
            }

            onNodeWithTag(EVENT_CARD_TEST_TAG)
                .assertIsDisplayed()

            onNodeWithText(event.name)
                .assertIsDisplayed()

            onNodeWithText(event.description)
                .assertIsDisplayed()

            onNodeWithText(
                getRemainingDateInfo(
                    event.startDate,
                    event.endDate,
                    this.activity,
                )
            ).assertIsDisplayed()
        }
    }

    @Test
    fun eventCardIsClicked_NavigateToEventDetailsPerformed() {
        val event = getEventStub("123")
        var isClickedWithCorrectId = false
        val onClick = { cardEventId: String ->
            if (cardEventId == event.id) {
                isClickedWithCorrectId = true
            }
        }

        composeTestRule.apply {
            setContent {
                SimbirSoftMobileTheme {
                    EventCard(
                        event = event,
                        onClick = { onClick.invoke(it) },
                    )
                }
            }

            onNodeWithTag(EVENT_CARD_TEST_TAG)
                .assertIsDisplayed()
                .performClick()

            assert(isClickedWithCorrectId)
        }
    }

    @Composable
    fun NewsScreenWithStubState(newsState: NewsState) {
        SimbirSoftMobileTheme {
            NewsScreen(
                state = newsState,
                onEvent = {},
                sideEffect = NewsSideEffect.NoEffect,
                navigateToEventDetails = {},
                navigateToSettings = {},
            )
        }
    }

    companion object {
        private fun getTitleStub(id: String) = "New's title $id"

        private val longDescription = StringBuilder().apply {
            repeat(20) {
                append("long description")
            }
        }.toString()


        private fun getEventStub(id: String) = EventShortUi(
            id = id,
            name = getTitleStub(id),
            123312231L,
            123312232L,
            longDescription,
            -1,
            "",
            "1",
            123123123132L,
        )

        private fun getSuccessfulStateStub(newsListSize: Int = 10) = NewsState(
            isLoading = false,
            error = null,
            news = List(newsListSize) {
                getEventStub(it.toString())
            },
        )

        private val LOADING_STATE_STUB = NewsState(
            isLoading = true,
            error = null,
            news = emptyList(),
        )

        private val ERROR_STUB = UiText.DynamicString("Test error")
        private val ERROR_STATE_STUB = NewsState(
            isLoading = false,
            error = ERROR_STUB,
            news = emptyList(),
        )
    }
}
