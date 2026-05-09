package org.wit.habit.helpers

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import org.wit.habit.model.Habit
import timber.log.Timber

class HabitStore(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "habit_prefs"
        private const val KEY_HABITS = "habits"
    }

    fun findAll(): List<Habit> {
        return try {
            val json = prefs.getString(KEY_HABITS, "[]") ?: "[]"
            if (json.isBlank()) return emptyList()
            val habits = mutableListOf<Habit>()
            val array = JSONArray(json)
            for (i in 0 until array.length()) {
                habits.add(parseHabit(array.getJSONObject(i)))
            }
            habits
        } catch (e: Exception) {
            Timber.e(e, "Failed to load habits, returning empty list")
            emptyList()
        }
    }

    fun findById(id: Long): Habit? {
        return findAll().find { it.id == id }
    }

    fun create(habit: Habit) {
        val habits = findAll().toMutableList()
        habits.add(habit)
        save(habits)
        Timber.i("HabitStore created habit: ${habit.name} (id=${habit.id})")
    }

    fun update(habit: Habit) {
        val habits = findAll().toMutableList()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
            save(habits)
            Timber.i("HabitStore updated habit: ${habit.name} (id=${habit.id})")
        }
    }

    fun delete(habit: Habit) {
        val habits = findAll().toMutableList()
        habits.removeAll { it.id == habit.id }
        save(habits)
        Timber.i("HabitStore deleted habit: ${habit.name} (id=${habit.id})")
    }

    fun checkIn(habit: Habit, date: String) {
        val current = habit.checkInCounts[date] ?: 0
        habit.checkInCounts[date] = current + 1
        update(habit)
        Timber.i("HabitStore checked in habit: ${habit.name} on $date (count=${current + 1})")
    }

    fun clearAll() {
        prefs.edit().remove(KEY_HABITS).apply()
        Timber.i("HabitStore cleared all data")
    }

    fun cancelCheckIn(habit: Habit, date: String) {
        val current = habit.checkInCounts[date] ?: 0
        if (current <= 1) {
            habit.checkInCounts.remove(date)
        } else {
            habit.checkInCounts[date] = current - 1
        }
        update(habit)
        Timber.i("HabitStore cancelled check-in for habit: ${habit.name} on $date")
    }

    private fun save(habits: List<Habit>) {
        try {
            val array = JSONArray()
            habits.forEach { array.put(toJson(it)) }
            prefs.edit().putString(KEY_HABITS, array.toString()).apply()
        } catch (e: Exception) {
            Timber.e(e, "Failed to save habits")
        }
    }

    private fun toJson(habit: Habit): JSONObject {
        val obj = JSONObject()
        obj.put("id", habit.id)
        obj.put("name", habit.name)
        obj.put("description", habit.description)
        obj.put("createdDate", habit.createdDate)
        obj.put("icon", habit.icon)
        obj.put("color", habit.color)
        val counts = JSONObject()
        habit.checkInCounts.forEach { (date, count) -> counts.put(date, count) }
        obj.put("checkInCounts", counts)
        return obj
    }

    private fun parseHabit(obj: JSONObject): Habit {
        val counts = mutableMapOf<String, Int>()
        val countsObj = obj.optJSONObject("checkInCounts")
        if (countsObj != null) {
            val keys = countsObj.keys()
            while (keys.hasNext()) {
                val key = keys.next()
                counts[key] = countsObj.getInt(key)
            }
        } else {
            val arr = obj.optJSONArray("checkInDates")
            if (arr != null) {
                for (i in 0 until arr.length()) {
                    counts[arr.getString(i)] = 1
                }
            }
        }
        return Habit(
            id = obj.optLong("id", System.currentTimeMillis()),
            name = obj.optString("name", ""),
            description = obj.optString("description", ""),
            createdDate = obj.optString("createdDate", DateUtils.today()),
            checkInCounts = counts,
            targetCount = obj.optInt("targetCount", 1),
            icon = obj.optString("icon", "✅"),
            color = obj.optString("color", "blue")
        )
    }
}
