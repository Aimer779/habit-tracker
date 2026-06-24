package org.wit.habit.util

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import org.wit.habit.ui.theme.*

object HabitColorHelper {
    @Composable
    fun getColor(colorKey: String): Color = when (colorKey) {
        "blue" -> if (isSystemInDarkTheme()) HabitBlueDark else HabitBlue
        "orange" -> if (isSystemInDarkTheme()) HabitOrangeDark else HabitOrange
        "green" -> if (isSystemInDarkTheme()) HabitGreenDark else HabitGreen
        "pink" -> if (isSystemInDarkTheme()) HabitPinkDark else HabitPink
        "purple" -> if (isSystemInDarkTheme()) HabitPurpleDark else HabitPurple
        "teal" -> if (isSystemInDarkTheme()) HabitTealDark else HabitTeal
        "yellow" -> if (isSystemInDarkTheme()) HabitYellowDark else HabitYellow
        "red" -> if (isSystemInDarkTheme()) HabitRedDark else HabitRed
        else -> if (isSystemInDarkTheme()) HabitBlueDark else HabitBlue
    }
}
