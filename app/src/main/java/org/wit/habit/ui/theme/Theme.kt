package org.wit.habit.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import org.wit.habit.data.local.ThemeStore

private data class ThemePalette(
    val primary: androidx.compose.ui.graphics.Color,
    val onPrimary: androidx.compose.ui.graphics.Color,
    val primaryContainer: androidx.compose.ui.graphics.Color,
    val onPrimaryContainer: androidx.compose.ui.graphics.Color,
    val darkPrimaryContainer: androidx.compose.ui.graphics.Color,
    val darkOnPrimaryContainer: androidx.compose.ui.graphics.Color
)

private val themePalettes = mapOf(
    "mint" to ThemePalette(MintPrimary, White, MintPrimaryContainer, MintOnPrimaryContainer, MintDarkPrimaryContainer, MintDarkOnPrimaryContainer),
    "blue" to ThemePalette(BluePrimary, White, BluePrimaryContainer, BlueOnPrimaryContainer, BlueDarkPrimaryContainer, BlueDarkOnPrimaryContainer),
    "red" to ThemePalette(RedPrimary, White, RedPrimaryContainer, RedOnPrimaryContainer, RedDarkPrimaryContainer, RedDarkOnPrimaryContainer),
    "green" to ThemePalette(GreenPrimary, White, GreenPrimaryContainer, GreenOnPrimaryContainer, GreenDarkPrimaryContainer, GreenDarkOnPrimaryContainer),
    "purple" to ThemePalette(PurplePrimary, White, PurplePrimaryContainer, PurpleOnPrimaryContainer, PurpleDarkPrimaryContainer, PurpleDarkOnPrimaryContainer),
    "yellow" to ThemePalette(YellowPrimary, Black, YellowPrimaryContainer, YellowOnPrimaryContainer, YellowDarkPrimaryContainer, YellowDarkOnPrimaryContainer)
)

private fun habitLightColorScheme(themeKey: String): ColorScheme {
    val palette = themePalettes[themeKey] ?: themePalettes.getValue("mint")
    return lightColorScheme(
        primary = palette.primary,
        onPrimary = palette.onPrimary,
        primaryContainer = palette.primaryContainer,
        onPrimaryContainer = palette.onPrimaryContainer,
        secondary = palette.primary,
        onSecondary = palette.onPrimary,
        secondaryContainer = palette.primaryContainer,
        onSecondaryContainer = palette.onPrimaryContainer,
        tertiary = GreenPrimary,
        onTertiary = White,
        tertiaryContainer = GreenPrimaryContainer,
        onTertiaryContainer = GreenOnPrimaryContainer,
        background = LightBackground,
        onBackground = LightOnBackground,
        surface = LightSurface,
        onSurface = LightOnSurface,
        surfaceVariant = LightSurfaceVariant,
        onSurfaceVariant = LightOnSurfaceVariant,
        outline = LightOutline,
        error = RedPrimary,
        onError = White,
        errorContainer = RedPrimaryContainer,
        onErrorContainer = RedOnPrimaryContainer
    )
}

private fun habitDarkColorScheme(themeKey: String): ColorScheme {
    val palette = themePalettes[themeKey] ?: themePalettes.getValue("mint")
    return darkColorScheme(
        primary = palette.primary,
        onPrimary = palette.onPrimary,
        primaryContainer = palette.darkPrimaryContainer,
        onPrimaryContainer = palette.darkOnPrimaryContainer,
        secondary = palette.primary,
        onSecondary = palette.onPrimary,
        secondaryContainer = palette.darkPrimaryContainer,
        onSecondaryContainer = palette.darkOnPrimaryContainer,
        tertiary = GreenPrimary,
        onTertiary = White,
        tertiaryContainer = GreenDarkPrimaryContainer,
        onTertiaryContainer = GreenDarkOnPrimaryContainer,
        background = DarkBackground,
        onBackground = DarkOnBackground,
        surface = DarkSurface,
        onSurface = DarkOnSurface,
        surfaceVariant = DarkSurfaceVariant,
        onSurfaceVariant = DarkOnSurfaceVariant,
        outline = DarkOutline,
        error = DarkError,
        onError = Black,
        errorContainer = DarkErrorContainer,
        onErrorContainer = DarkOnErrorContainer
    )
}

@Composable
fun HabitTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val context = LocalContext.current
    val themeKey = ThemeStore.getCurrentThemeKey(context)
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> habitDarkColorScheme(themeKey)
        else -> habitLightColorScheme(themeKey)
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
