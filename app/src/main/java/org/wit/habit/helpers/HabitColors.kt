package org.wit.habit.helpers

import org.wit.habit.R

object HabitColors {
    fun getColorRes(colorKey: String): Int = when (colorKey) {
        "blue" -> R.color.habit_blue
        "orange" -> R.color.habit_orange
        "green" -> R.color.habit_green
        "pink" -> R.color.habit_pink
        "purple" -> R.color.habit_purple
        "teal" -> R.color.habit_teal
        "yellow" -> R.color.habit_yellow
        "red" -> R.color.habit_red
        else -> R.color.habit_blue
    }

    val colorOptions = listOf(
        "blue" to "Blue",
        "orange" to "Orange",
        "green" to "Green",
        "pink" to "Pink",
        "purple" to "Purple",
        "teal" to "Teal",
        "yellow" to "Yellow",
        "red" to "Red"
    )

    val iconOptions = listOf(
        "✅", "📖", "🍽️", "🏃", "💧", "🎸",
        "💻", "🧘", "🌱", "😴", "🎨", "🎵"
    )
}
