package org.wit.habit.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = MintPrimary,
    primaryContainer = MintPrimaryContainer,
    secondary = BluePrimary,
    secondaryContainer = BluePrimaryContainer,
    tertiary = GreenPrimary,
    tertiaryContainer = GreenPrimaryContainer,
    error = RedPrimary,
    errorContainer = RedPrimaryContainer
)

private val DarkColorScheme = darkColorScheme(
    primary = MintPrimary,
    primaryContainer = MintPrimaryContainer,
    secondary = BluePrimary,
    secondaryContainer = BluePrimaryContainer,
    tertiary = GreenPrimary,
    tertiaryContainer = GreenPrimaryContainer,
    error = RedPrimary,
    errorContainer = RedPrimaryContainer
)

@Composable
fun HabitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
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
