package org.wit.habit.model

data class Habit(
    var id: Long = System.currentTimeMillis(),
    var title: String = "",
    var description: String = "",
    var createdTime: Long = System.currentTimeMillis(),
    var checkInDates: MutableSet<String> = mutableSetOf()
)
