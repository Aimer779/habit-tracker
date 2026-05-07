package org.wit.habit.helpers

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONObject
import org.wit.habit.model.Habit

class HabitStore(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "habit_prefs"
        private const val KEY_HABITS = "habits"
    }

    fun findAll(): List<Habit> {
        val json = prefs.getString(KEY_HABITS, "[]") ?: "[]"
        val habits = mutableListOf<Habit>()
        val array = JSONArray(json)
        for (i in 0 until array.length()) {
            habits.add(parseHabit(array.getJSONObject(i)))
        }
        return habits
    }

    fun findById(id: Long): Habit? {
        return findAll().find { it.id == id }
    }

    fun create(habit: Habit) {
        val habits = findAll().toMutableList()
        habits.add(habit)
        save(habits)
    }

    fun update(habit: Habit) {
        val habits = findAll().toMutableList()
        val index = habits.indexOfFirst { it.id == habit.id }
        if (index != -1) {
            habits[index] = habit
            save(habits)
        }
    }

    fun delete(habit: Habit) {
        val habits = findAll().toMutableList()
        habits.removeAll { it.id == habit.id }
        save(habits)
    }

    fun checkIn(habit: Habit, date: String) {
        habit.checkInDates.add(date)
        update(habit)
    }

    private fun save(habits: List<Habit>) {
        val array = JSONArray()
        habits.forEach { array.put(toJson(it)) }
        prefs.edit().putString(KEY_HABITS, array.toString()).apply()
    }

    private fun toJson(habit: Habit): JSONObject {
        val obj = JSONObject()
        obj.put("id", habit.id)
        obj.put("title", habit.title)
        obj.put("description", habit.description)
        obj.put("createdTime", habit.createdTime)
        val dates = JSONArray()
        habit.checkInDates.forEach { dates.put(it) }
        obj.put("checkInDates", dates)
        return obj
    }

    private fun parseHabit(obj: JSONObject): Habit {
        val dates = mutableSetOf<String>()
        val arr = obj.getJSONArray("checkInDates")
        for (i in 0 until arr.length()) {
            dates.add(arr.getString(i))
        }
        return Habit(
            id = obj.getLong("id"),
            title = obj.getString("title"),
            description = obj.getString("description"),
            createdTime = obj.getLong("createdTime"),
            checkInDates = dates
        )
    }
}
