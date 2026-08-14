package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanPrimary,
    onPrimary = PureBlack,
    secondary = SurfaceNavy,
    onSecondary = TextPrimary,
    tertiary = HealthyGreen,
    background = DeepBackground,
    onBackground = TextPrimary,
    surface = SurfaceNavy,
    onSurface = TextPrimary,
    surfaceVariant = CardBorderNavy,
    onSurfaceVariant = TextSecondary,
    error = CriticalRed,
    onError = PureWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Disabled to enforce consistent medical-grade brand colors
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}
