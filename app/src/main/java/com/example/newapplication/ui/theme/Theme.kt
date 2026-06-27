package com.example.newapplication.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = MeteoPrimary,
    onPrimary = MeteoOnPrimary,
    primaryContainer = MeteoPrimaryContainer,
    onPrimaryContainer = MeteoOnPrimaryContainer,
    inversePrimary = MeteoInversePrimary,

    secondary = MeteoSecondary,
    onSecondary = MeteoOnSecondary,
    secondaryContainer = MeteoSecondaryContainer,
    onSecondaryContainer = MeteoOnSecondaryContainer,

    tertiary = MeteoTertiary,
    onTertiary = MeteoOnTertiary,
    tertiaryContainer = MeteoTertiaryContainer,
    onTertiaryContainer = MeteoOnTertiaryContainer,

    background = MeteoBackground,
    onBackground = MeteoOnBackground,
    surface = MeteoSurface,
    onSurface = MeteoOnSurface,
    surfaceVariant = MeteoSurfaceVariant,
    onSurfaceVariant = MeteoOnSurfaceVariant,
    surfaceTint = MeteoSurfaceTint,
    inverseSurface = MeteoInverseSurface,
    inverseOnSurface = MeteoInverseOnSurface,

    outline = MeteoOutline,
    outlineVariant = MeteoOutlineVariant,
    scrim = MeteoScrim,

    error = MeteoError,
    onError = MeteoOnError,
    errorContainer = MeteoErrorContainer,
    onErrorContainer = MeteoOnErrorContainer

)

private val LightColorScheme = lightColorScheme(
    primary = MeteoPrimary, // Keeps the core blue brand color
    onPrimary = Color(0xFFFFFFFF),
    primaryContainer = Color(0xFFD4E3FF),
    onPrimaryContainer = Color(0xFF001C3B),
    inversePrimary = MeteoInversePrimary,

    secondary = Color(0xFF006D33), // Slightly darker green for light bg contrast
    onSecondary = Color(0xFFFFFFFF),
    secondaryContainer = Color(0xFF91F4A6),
    onSecondaryContainer = Color(0xFF00210B),

    tertiary = Color(0xFF7C5A00), // Slightly darker gold for light bg contrast
    onTertiary = Color(0xFFFFFFFF),
    tertiaryContainer = Color(0xFFFFDF9D),
    onTertiaryContainer = Color(0xFF271B00),

    background = Color(0xFFFDF8FF), // Crisp clean background
    onBackground = Color(0xFF1A1C1E),
    surface = Color(0xFFFDF8FF),
    onSurface = Color(0xFF1A1C1E),
    surfaceVariant = Color(0xFFDFE2EB),
    onSurfaceVariant = Color(0xFF43474E),
    surfaceTint = MeteoSurfaceTint,
    inverseSurface = Color(0xFF2F3033),
    inverseOnSurface = Color(0xFFF1F0F4),

    outline = Color(0xFF73777F),
    outlineVariant = Color(0xFFC3C7D0),
    scrim = Color(0xFF000000),

    error = MeteoError,
    onError = MeteoOnError,
    errorContainer = MeteoErrorContainer,
    onErrorContainer = MeteoOnErrorContainer
)

@Composable
fun NewApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Dynamic color is available on Android 12+
    dynamicColor: Boolean = true,
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }

        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}