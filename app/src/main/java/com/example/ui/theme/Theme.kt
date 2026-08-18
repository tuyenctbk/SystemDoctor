package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
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

private val LightColorScheme = lightColorScheme(
    primary = Color(0xFF0288D1),       // Premium Medical Blue
    onPrimary = PureWhite,
    secondary = Color(0xFFE1F5FE),     // Light Blue Soft Surface
    onSecondary = Color(0xFF01579B),
    tertiary = Color(0xFF2E7D32),       // Safe Green
    background = Color(0xFFF4F6F9),    // Light Grey Medical Background
    onBackground = Color(0xFF1A1C1E),  // Dark Text
    surface = Color(0xFFFFFFFF),       // Card White
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFE0E2E5),// Soft grey border
    onSurfaceVariant = Color(0xFF5D6064),
    error = Color(0xFFC62828),
    onError = PureWhite
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isAppDarkMode,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val colors = if (darkTheme) DarkColorScheme else LightColorScheme
    MaterialTheme(
        colorScheme = colors,
        typography = Typography,
        content = content
    )
}
