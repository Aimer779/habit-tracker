package org.wit.habit.ui.compose

import androidx.compose.ui.graphics.Color
import org.wit.habit.ui.theme.*

object HabitColorHelper {
    fun getColor(colorKey: String): Color = when (colorKey) {
        "blue" -> HabitBlue
        "orange" -> HabitOrange
        "green" -> HabitGreen
        "pink" -> HabitPink
        "purple" -> HabitPurple
        "teal" -> HabitTeal
        "yellow" -> HabitYellow
        "red" -> HabitRed
        else -> HabitBlue
    }
}
