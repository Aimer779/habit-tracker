package org.wit.habit.data.local

import android.content.Context
import org.wit.habit.R

data class ThemeOption(
    val key: String,
    val displayNameRes: Int,
    val themeRes: Int,
    val swatchColorRes: Int
)

object ThemeStore {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "selected_theme"

    private val defaultOption = ThemeOption(
        key = "mint",
        displayNameRes = R.string.theme_mint,
        themeRes = R.style.Theme_Habit_Mint,
        swatchColorRes = R.color.mint_primary
    )

    private val options = listOf(
        defaultOption,
        ThemeOption(
            key = "blue",
            displayNameRes = R.string.theme_blue,
            themeRes = R.style.Theme_Habit_Blue,
            swatchColorRes = R.color.blue_primary
        ),
        ThemeOption(
            key = "red",
            displayNameRes = R.string.theme_red,
            themeRes = R.style.Theme_Habit_Red,
            swatchColorRes = R.color.red_primary
        ),
        ThemeOption(
            key = "green",
            displayNameRes = R.string.theme_green,
            themeRes = R.style.Theme_Habit_Green,
            swatchColorRes = R.color.green_primary
        ),
        ThemeOption(
            key = "purple",
            displayNameRes = R.string.theme_purple,
            themeRes = R.style.Theme_Habit_Purple,
            swatchColorRes = R.color.purple_primary
        ),
        ThemeOption(
            key = "yellow",
            displayNameRes = R.string.theme_yellow,
            themeRes = R.style.Theme_Habit_Yellow,
            swatchColorRes = R.color.yellow_primary
        )
    )

    val themeOptions: List<ThemeOption> = options

    fun getThemeOption(key: String): ThemeOption {
        return options.find { it.key == key } ?: defaultOption
    }

    fun getThemeRes(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val key = prefs.getString(KEY_THEME, defaultOption.key) ?: defaultOption.key
        return getThemeOption(key).themeRes
    }

    fun setTheme(context: Context, key: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME, key)
            .apply()
    }

    fun getCurrentThemeKey(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME, defaultOption.key) ?: defaultOption.key
    }
}
