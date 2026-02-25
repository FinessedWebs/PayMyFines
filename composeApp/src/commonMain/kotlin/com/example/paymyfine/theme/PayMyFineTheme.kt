package com.example.paymyfine.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MotionScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

private val LightColors = lightColorScheme(
    primary = PrimaryColor,
    onPrimary = TextColor,

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

private val DarkColors = darkColorScheme(
    primary = PrimaryColor,
    onPrimary = TextColor,

    secondary = AccentColor,
    onSecondary = OnPrimaryColor,

    error = ErrorRed,
    onError = OnPrimaryColor
)

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PayMyFineTheme(
    darkTheme: Boolean = false,
    content: @Composable () -> Unit
) {

    // 🔥 Add MotionScheme
    val motionScheme = remember {
        MotionScheme.standard()
    }

    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        motionScheme = motionScheme, // ✅ Added
        content = content
    )
}