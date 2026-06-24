package org.wit.habit.utils

import org.wit.habit.R

data class HabitColorOption(
    val key: String,
    val labelRes: Int,
    val colorRes: Int
)

object HabitColors {
    private val defaultOption = HabitColorOption(
        key = "blue",
        labelRes = R.string.color_blue,
        colorRes = R.color.habit_blue
    )

    private val options = listOf(
        defaultOption,
        HabitColorOption(
            key = "orange",
            labelRes = R.string.color_orange,
            colorRes = R.color.habit_orange
        ),
        HabitColorOption(
            key = "green",
            labelRes = R.string.color_green,
            colorRes = R.color.habit_green
        ),
        HabitColorOption(
            key = "pink",
            labelRes = R.string.color_pink,
            colorRes = R.color.habit_pink
        ),
        HabitColorOption(
            key = "purple",
            labelRes = R.string.color_purple,
            colorRes = R.color.habit_purple
        ),
        HabitColorOption(
            key = "teal",
            labelRes = R.string.color_teal,
            colorRes = R.color.habit_teal
        ),
        HabitColorOption(
            key = "yellow",
            labelRes = R.string.color_yellow,
            colorRes = R.color.habit_yellow
        ),
        HabitColorOption(
            key = "red",
            labelRes = R.string.color_red,
            colorRes = R.color.habit_red
        )
    )

    val colorOptions: List<HabitColorOption> = options

    fun getColorOption(key: String): HabitColorOption {
        return options.find { it.key == key } ?: defaultOption
    }

    fun getColorRes(colorKey: String): Int {
        return getColorOption(colorKey).colorRes
    }

    val iconOptions = listOf(
        "✅", "📖", "🍽️", "🏃", "💧", "🎸",
        "💻", "🧘", "🌱", "😴", "🎨", "🎵"
    )
}
