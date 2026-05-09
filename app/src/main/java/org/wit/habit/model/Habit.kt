package org.wit.habit.model

import org.wit.habit.helpers.DateUtils

data class Habit(
    var id: Long = System.currentTimeMillis(),
    var name: String = "",
    var description: String = "",
    var createdDate: String = DateUtils.today(),
    var checkInDates: MutableSet<String> = mutableSetOf(),
    var icon: String = "✅",
    var color: String = "blue"
)
