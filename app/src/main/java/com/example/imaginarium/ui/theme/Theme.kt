package com.example.imaginarium.ui.theme

import android.annotation.SuppressLint
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material.MaterialTheme
import androidx.compose.material.darkColors
import androidx.compose.material.lightColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.core.splashscreen.SplashScreen

private val DarkColorPalette = darkColors(
    primary = Color(0xFF26292D),
    primaryVariant = Color.White,
    secondary = Color(0xFF84ED0A),
    onSecondary = Color.Black,
    background = Color(0xFF27292D),
    onPrimary = Color(0xFFDADADA),
    surface = Color(0xFF27292D)

)

@SuppressLint("ConflictingOnColor")
private val LightColorPalette = lightColors(
    primary = Color.White,
    primaryVariant = Color.Black,
    secondary = Color(0xFF84ED0A),
    onSecondary = Color.White,
    onPrimary = Color(0xFF27292D),
    onBackground = Color.Black,
    surface = Color.White

    /* Other default colors to override
    background = Color.White,
    surface = Color.White,
    onPrimary = Color.White,
    onSecondary = Color.Black,
    onBackground = Color.Black,
    onSurface = Color.Black,
    */
)


@Composable
fun ImaginariumTheme(darkTheme: Boolean = isSystemInDarkTheme(), content: @Composable () -> Unit) {
    val colors = if (darkTheme) {
        DarkColorPalette
    } else {
        LightColorPalette
    }

    MaterialTheme(
        colors = colors,
        typography = Typography,
        shapes = Shapes,
        content = content
    )
}