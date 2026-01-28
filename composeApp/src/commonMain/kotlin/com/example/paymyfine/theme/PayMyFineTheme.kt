package com.example.paymyfine.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val LightColors = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = TextColor, // ✅ black text looks best on bright yellow

    primaryContainer = PrimaryLightColor,
    onPrimaryContainer = TextColor,

    secondary = AccentColor,
    onSecondary = OnPrimaryColor,

    background = BackgroundColor,
    onBackground = TextColor,

    surface = SurfaceColor,
    onSurface = TextColor,

    error = ErrorRed,
    onError = OnPrimaryColor
)

// Optional: you can define dark colors later if needed
private val DarkColors = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = TextColor,

    secondary = AccentColor,
    onSecondary = OnPrimaryColor,

    error = ErrorRed,
    onError = OnPrimaryColor
)

@Composable
fun PayMyFineTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        content = content
    )
}
