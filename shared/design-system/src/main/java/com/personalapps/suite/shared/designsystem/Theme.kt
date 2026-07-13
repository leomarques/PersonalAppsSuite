package com.personalapps.suite.shared.designsystem

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

data class Spacing(
    val extraSmall: Dp = 4.dp,
    val small: Dp = 8.dp,
    val medium: Dp = 16.dp,
    val large: Dp = 24.dp,
    val extraLarge: Dp = 32.dp
)

val LocalSpacing = staticCompositionLocalOf { Spacing() }

private val DarkColorScheme = darkColorScheme(
    primary = Indigo80,
    secondary = Orange80,
    background = DarkGray,
    surface = SurfaceDark,
    error = ErrorRed
)

private val LightColorScheme = lightColorScheme(
    primary = Indigo40,
    secondary = Orange40,
    background = LightGray,
    surface = SurfaceLight,
    error = ErrorRed
)

@Composable
fun PersonalAppsSuiteTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    CompositionLocalProvider(LocalSpacing provides Spacing()) {
        MaterialTheme(
            colorScheme = colorScheme,
            content = content
        )
    }
}
