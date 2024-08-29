package com.example.news.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.imageResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import coil.size.Size
import com.example.common_compose.theme.BlueGrey
import com.example.common_compose.theme.CircularProgressIndicator
import com.example.common_compose.theme.CommonText
import com.example.common_compose.theme.HeaderText
import com.example.common_compose.theme.SimbirSoftMobileTheme
import com.example.date.getRemainingDateInfo
import com.example.news.models.EventShortUi
import com.example.common_view.R as commonR
import com.example.news.R as newsR

const val EVENT_CARD_TEST_TAG = "EventCard test tag"

@Composable
fun EventCard(
    modifier: Modifier = Modifier,
    event: EventShortUi,
    onClick: (String) -> Unit,
) {
    Card(
        onClick = { onClick(event.id) },
        elevation = CardDefaults.cardElevation(
            defaultElevation = dimensionResource(id = newsR.dimen.event_card_elevation)
        ),
        shape = RoundedCornerShape(dimensionResource(id = newsR.dimen.event_card_radius)),
        colors = CardDefaults.elevatedCardColors(
            containerColor = MaterialTheme.colorScheme.background
        ),
        modifier = modifier
            .testTag(EVENT_CARD_TEST_TAG),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.fillMaxWidth()
        ) {

            val painter = rememberAsyncImagePainter(
                model = ImageRequest.Builder(LocalContext.current)
                    .data(event.photo)
                    .size(Size.ORIGINAL)
                    .crossfade(true)
                    .build()
            )
            when (painter.state) {
                AsyncImagePainter.State.Empty -> {

                }

                is AsyncImagePainter.State.Error -> {
                    Image(
                        ImageBitmap.imageResource(newsR.drawable.news_preview_not_found),
                        contentDescription = "Нельзя загрузить изображение"
                    )
                }

                is AsyncImagePainter.State.Loading -> {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(
                            start = dimensionResource(id = newsR.dimen.event_item_padding),
                            end = dimensionResource(id = newsR.dimen.event_item_padding),
                            top = dimensionResource(id = newsR.dimen.event_item_padding),
                        )
                    ) {
                        CircularProgressIndicator()
                        Image(
                            bitmap = ImageBitmap.imageResource(id = commonR.drawable.fade),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }

                is AsyncImagePainter.State.Success -> {
                    Box(
                        contentAlignment = Alignment.BottomCenter,
                        modifier = Modifier.padding(
                            start = dimensionResource(id = newsR.dimen.event_item_padding),
                            end = dimensionResource(id = newsR.dimen.event_item_padding),
                            top = dimensionResource(id = newsR.dimen.event_item_padding),
                        )
                    ) {
                        Image(
                            painter = painter,
                            contentDescription = event.name,
                            contentScale = ContentScale.FillWidth,
                            modifier = Modifier
                                .fillMaxWidth()
                        )
                        Image(
                            bitmap = ImageBitmap.imageResource(id = commonR.drawable.fade),
                            contentDescription = null,
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                }
            }

            HeaderText(
                text = event.name,
                style = TextStyle(lineHeight = dimensionResource(id = newsR.dimen.event_header_line_height).value.sp),
                textAlign = TextAlign.Center,
                color = BlueGrey,
                modifier = Modifier
                    .padding(
                        horizontal = dimensionResource(
                            id = newsR.dimen.event_item_title_padding_horizontal
                        )
                    )
            )
            Image(
                painter = painterResource(id = commonR.drawable.decoration_divider),
                contentDescription = null,
                contentScale = ContentScale.None,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = dimensionResource(id = newsR.dimen.event_item_spacer))
            )
            Box(
                modifier = Modifier.padding(
                    top = dimensionResource(id = newsR.dimen.event_description_padding_top),
                    bottom = dimensionResource(id = newsR.dimen.event_description_padding_bottom),
                    start = dimensionResource(id = newsR.dimen.event_description_padding_horizontal),
                    end = dimensionResource(id = newsR.dimen.event_description_padding_horizontal),
                )
            ) {
                CommonText(
                    text = event.description,
                    maxLines = 3,
                    textAlign = TextAlign.Center,
                    overflow = TextOverflow.Ellipsis,
                    color = colorResource(id = commonR.color.black70)
                )
            }
            Row(
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.tertiary)
                    .fillMaxWidth()
            ) {
                Icon(
                    painter = painterResource(id = commonR.drawable.ic_calendar),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.onTertiary,
                    modifier = Modifier.padding(
                        dimensionResource(id = newsR.dimen.event_date_icon_padding)
                    )
                )
                CommonText(
                    text = getRemainingDateInfo(
                        event.startDate,
                        event.endDate,
                        LocalContext.current,
                    ),
                    color = MaterialTheme.colorScheme.onTertiary,
                    fontSize = dimensionResource(
                        id = commonR.dimen.event_details_additional_text_size
                    ).value.sp,
                )
            }
        }
    }
}

@Preview(showBackground = true, locale = "ru", showSystemUi = true)
@Composable
private fun EventCardPreview() {
    val event = EventShortUi(
        id = "123",
        "Конкурс по вокальному пению в детском доме №6",
        startDate = 200000L,
        endDate = 2000001L,
        "Дубовская школа-интернат для детей с ограниченными возможностями здоровья стала первой в области области области области области области области области",
        status = 213,
        photo = "",
        category = "2",
        createdAt = 123213132L,
    )

    SimbirSoftMobileTheme {
        EventCard(event = event, modifier = Modifier) {

        }
    }
}
