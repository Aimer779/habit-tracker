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
        else -> R.color.habit_blue
    }

    val colorOptions = listOf(
        "blue" to "蓝色",
        "orange" to "橙色",
        "green" to "绿色",
        "pink" to "粉色",
        "purple" to "紫色",
        "teal" to "青色"
    )

    val iconOptions = listOf(
        "✅", "📖", "🍽️", "🏃", "💧", "🎸",
        "💻", "🧘", "🌱", "😴", "🎨", "🎵"
    )
}
