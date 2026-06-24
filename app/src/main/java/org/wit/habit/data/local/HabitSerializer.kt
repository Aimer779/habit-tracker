package org.wit.habit.data.local

import org.json.JSONObject
import org.wit.habit.models.Habit
import org.wit.habit.utils.DateUtils

object HabitSerializer {
    private const val KEY_ID = "id"
    private const val KEY_NAME = "name"
    private const val KEY_DESCRIPTION = "description"
    private const val KEY_CREATED_DATE = "createdDate"
    private const val KEY_ICON = "icon"
    private const val KEY_COLOR = "color"
    private const val KEY_TARGET_COUNT = "targetCount"
    private const val KEY_CHECK_IN_COUNTS = "checkInCounts"
    private const val KEY_CHECK_IN_DATES = "checkInDates"

    fun toJson(habit: Habit): JSONObject {
        val obj = JSONObject()
        obj.put(KEY_ID, habit.id)
        obj.put(KEY_NAME, habit.name)
        obj.put(KEY_DESCRIPTION, habit.description)
        obj.put(KEY_CREATED_DATE, habit.createdDate)
        obj.put(KEY_ICON, habit.icon)
        obj.put(KEY_COLOR, habit.color)
        obj.put(KEY_TARGET_COUNT, habit.targetCount)
        val counts = JSONObject()
        habit.checkInCounts.forEach { (date, count) -> counts.put(date, count) }
        obj.put(KEY_CHECK_IN_COUNTS, counts)
        return obj
    }

    fun fromJson(obj: JSONObject): Habit {
        val counts = parseCheckInCounts(obj)
        return Habit(
            id = obj.optLong(KEY_ID, System.currentTimeMillis()),
            name = obj.optString(KEY_NAME, ""),
            description = obj.optString(KEY_DESCRIPTION, ""),
            createdDate = obj.optString(KEY_CREATED_DATE, DateUtils.today()),
            checkInCounts = counts,
            targetCount = obj.optInt(KEY_TARGET_COUNT, 1),
            icon = obj.optString(KEY_ICON, "✅"),
            color = obj.optString(KEY_COLOR, "blue")
        )
    }

    private fun parseCheckInCounts(obj: JSONObject): Map<String, Int> {
        val counts = mutableMapOf<String, Int>()
        val countsObj = obj.optJSONObject(KEY_CHECK_IN_COUNTS)
        if (countsObj != null) {
            val keys = countsObj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                counts[key] = countsObj.optInt(key, 0)
            }
        } else {
            val arr = obj.optJSONArray(KEY_CHECK_IN_DATES)
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    val date = arr.optString(i, "")
                    if (date.isNotEmpty()) {
                        counts[date] = 1
                    }
                }
            }
        }
        return counts
    }
}
