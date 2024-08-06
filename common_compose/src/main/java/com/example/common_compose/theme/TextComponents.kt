package com.example.common_compose.theme

import androidx.compose.foundation.text.BasicText
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.LocalTextStyle
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.takeOrElse
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import androidx.core.content.res.ResourcesCompat
import com.example.common_view.R

@Composable
fun HeaderText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = 21.sp,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit) = {},
    style: TextStyle = LocalTextStyle.current,
) {
    val typeface = ResourcesCompat.getFont(
        LocalContext.current,
        R.font.officinasansextraboldscc,
    )

    BasicText(
        text,
        modifier,
        style.merge(
            color = color.takeOrElse {
                style.color.takeOrElse {
                    LocalContentColor.current
                }
            },
            fontSize = fontSize,
            textAlign = textAlign ?: TextAlign.Unspecified,
            fontFamily = typeface?.let {
                FontFamily(
                    typeface = it
                )
            }

        ),
        onTextLayout,
        overflow,
        softWrap,
        maxLines,
        minLines
    )
}


@Composable
fun CommonText(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = Color.Unspecified,
    fontSize: TextUnit = TextUnit.Unspecified,
    textAlign: TextAlign? = null,
    overflow: TextOverflow = TextOverflow.Clip,
    softWrap: Boolean = true,
    maxLines: Int = Int.MAX_VALUE,
    minLines: Int = 1,
    onTextLayout: ((TextLayoutResult) -> Unit) = {},
    style: TextStyle = LocalTextStyle.current.merge(
        fontFamily = RobotoFontFamily
    ),
) {
    BasicText(
        text,
        modifier,
        style.merge(
            color = color.takeOrElse {
                style.color.takeOrElse {
                    LocalContentColor.current
                }
            },
            fontSize = fontSize,
            textAlign = textAlign ?: TextAlign.Unspecified,
        ),
        onTextLayout,
        overflow,
        softWrap,
        maxLines,
        minLines
    )
}
