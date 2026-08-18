package com.example.ui.theme

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color

var isAppDarkMode by mutableStateOf(true)

// Primary Medical Brand Palette
val CyanPrimary get() = if (isAppDarkMode) Color(0xFF00E5FF) else Color(0xFF0288D1)       // Glowing Tech Cyan / Premium Blue
val DeepBackground get() = if (isAppDarkMode) Color(0xFF070B14) else Color(0xFFF4F6F9)    // Slate Black / Light Grey
val SurfaceNavy get() = if (isAppDarkMode) Color(0xFF0F1626) else Color(0xFFFFFFFF)       // Deep Tech Surface / Card White
val CardBorderNavy get() = if (isAppDarkMode) Color(0xFF1E293B) else Color(0xFFE0E2E5)    // Border color

// Signal and Status Colors (Medical grade)
val HealthyGreen = Color(0xFF00E676)      // Perfect status
val WarningAmber = Color(0xFFFFA000)      // Requires attention
val CriticalRed = Color(0xFFFF1744)       // Critical status

// Text and Accents
val TextPrimary get() = if (isAppDarkMode) Color(0xFFF1F5F9) else Color(0xFF1A1C1E)       // Soft White / Dark Text
val TextSecondary get() = if (isAppDarkMode) Color(0xFF94A3B8) else Color(0xFF5D6064)     // Cool Muted Grey / Muted Grey
val MedicalGlowBlue get() = if (isAppDarkMode) Color(0x3300E5FF) else Color(0x330288D1)   // Glowing border tint
val PureWhite get() = if (isAppDarkMode) Color(0xFFFFFFFF) else Color(0xFF000000)
val PureBlack get() = if (isAppDarkMode) Color(0xFF000000) else Color(0xFFFFFFFF)
