package org.wit.habit.models

import org.wit.habit.utils.DateUtils

data class Habit(
    val id: Long = System.currentTimeMillis(),
    val name: String = "",
    val description: String = "",
    val createdDate: String = DateUtils.today(),
    val checkInCounts: Map<String, Int> = emptyMap(),
    val targetCount: Int = 1,
    val icon: String = "✅",
    val color: String = "blue"
)
