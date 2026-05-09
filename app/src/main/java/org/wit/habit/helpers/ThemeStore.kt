package org.wit.habit.helpers

import android.content.Context
import org.wit.habit.R

object ThemeStore {
    private const val PREFS_NAME = "theme_prefs"
    private const val KEY_THEME = "selected_theme"

    private val themes = mapOf(
        "mint" to R.style.Theme_Habit_Mint,
        "blue" to R.style.Theme_Habit_Blue,
        "red" to R.style.Theme_Habit_Red,
        "green" to R.style.Theme_Habit_Green,
        "purple" to R.style.Theme_Habit_Purple,
        "yellow" to R.style.Theme_Habit_Yellow,
    )

    val themeOptions = listOf(
        "mint" to "Mint",
        "blue" to "Sky Blue",
        "red" to "Vibrant Red",
        "green" to "Nature Green",
        "purple" to "Dream Purple",
        "yellow" to "Sunny Yellow",
    )

    fun getThemeRes(context: Context): Int {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val key = prefs.getString(KEY_THEME, "mint") ?: "mint"
        return themes[key] ?: R.style.Theme_Habit_Mint
    }

    fun setTheme(context: Context, key: String) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .edit()
            .putString(KEY_THEME, key)
            .apply()
    }

    fun getCurrentThemeKey(context: Context): String {
        return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            .getString(KEY_THEME, "mint") ?: "mint"
    }
}
