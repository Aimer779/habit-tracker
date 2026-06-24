package org.wit.habit.models

import org.wit.habit.util.DateUtils

data class Habit(
    var id: Long = System.currentTimeMillis(),
    var name: String = "",
    var description: String = "",
    var createdDate: String = DateUtils.today(),
    var checkInCounts: MutableMap<String, Int> = mutableMapOf(),
    var targetCount: Int = 1,
    var icon: String = "✅",
    var color: String = "blue"
)
