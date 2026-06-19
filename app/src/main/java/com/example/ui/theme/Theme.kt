package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = TacticalOrange,
    secondary = SteelBlue,
    tertiary = SolarYellow,
    background = GunmetalDark,
    surface = SlateGrey,
    onPrimary = Color.White,
    onSecondary = Color.White,
    onTertiary = GunmetalDark,
    onBackground = OnSurfaceWhite,
    onSurface = OnSurfaceWhite,
    surfaceVariant = CyberGrey,
    onSurfaceVariant = SubtitleGrey,
    error = Color(0xFFFF5252)
)

// Force tactical dark theme by default for gaming aesthetic, but allow normal overrides if requested.
@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = true, // Force dark theme for PUBG gaming look
    content: @Composable () -> Unit
) {
    val colorScheme = DarkColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
