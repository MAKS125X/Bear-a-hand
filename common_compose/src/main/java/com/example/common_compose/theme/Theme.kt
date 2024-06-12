package com.example.common_compose.theme

import android.app.Activity
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.googlefonts.Font
import androidx.compose.ui.text.googlefonts.GoogleFont
import androidx.core.view.WindowCompat
import com.example.common_view.R

private val LightColorScheme
    @Composable
    get() = lightColorScheme(
        primary = colorResource(id = R.color.green_leaf),
        inversePrimary = colorResource(id = R.color.cool_grey),
        onPrimary = colorResource(id = R.color.white),
        secondary = colorResource(id = R.color.pale_grey),
        onSecondary = colorResource(id = R.color.light_olive_green),
        tertiary = colorResource(id = R.color.turtle_green),
        onTertiary = colorResource(id = R.color.white),
        background = colorResource(id = R.color.white),
        onBackground = colorResource(id = R.color.black87),
        surface = colorResource(id = R.color.light_grey_2),
        onSurface = colorResource(id = R.color.black54),
    )

val BlueGrey
    @Composable
    get() = colorResource(id = R.color.blue_grey)

val DividerColor
    @Composable
    get() = colorResource(id = R.color.cool_grey)

val HelperTextColor
    @Composable
    get() = colorResource(id = R.color.black38)

val SimbirSoftMobileFont = FontFamily(
    fonts = listOf(
        Font(
            resId = R.font.roboto,
            weight = FontWeight.Normal,
            style = FontStyle.Normal,
        )
    )
)

val FontName = GoogleFont("Roboto")

val Provider = GoogleFont.Provider(
    providerAuthority = "com.google.android.gms.fonts",
    providerPackage = "com.google.android.gms",
    certificates = R.array.com_google_android_gms_fonts_certs
)

val RobotoFontFamily = FontFamily(
    Font(googleFont = FontName, fontProvider = Provider)
)

@Composable
fun SimbirSoftMobileTheme(content: @Composable () -> Unit) {
    val colorScheme = LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        content = content,
    )
}
