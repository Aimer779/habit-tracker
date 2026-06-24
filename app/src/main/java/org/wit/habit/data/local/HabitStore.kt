package org.wit.habit.data.local

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONArray
import org.json.JSONException
import org.wit.habit.models.Habit
import timber.log.Timber

class HabitStore(private val context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)

    companion object {
        private const val PREFS_NAME = "habit_prefs"
        private const val KEY_HABITS = "habits"
        private const val KEY_HABITS_BACKUP = "habits_backup"

        internal fun parseHabits(json: String): List<Habit> {
            val habits = mutableListOf<Habit>()
            return try {
                val array = JSONArray(json)
                for (i in 0 until array.length()) {
                    try {
                        val obj = array.getJSONObject(i)
                        habits.add(HabitSerializer.fromJson(obj))
                    } catch (e: Exception) {
                        Timber.e(e, "Failed to parse habit at index $i, skipping")
                    }
                }
                habits
            } catch (e: JSONException) {
                Timber.e(e, "Failed to parse habits array, returning empty list")
                emptyList()
            }
        }
    }

    fun findAll(): List<Habit> {
        val json = prefs.getString(KEY_HABITS, "[]") ?: "[]"
        if (json.isBlank()) return emptyList()
        return parseHabits(json)
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
        val newCounts = habit.checkInCounts + (date to current + 1)
        update(habit.copy(checkInCounts = newCounts))
        Timber.i("HabitStore checked in habit: ${habit.name} on $date (count=${current + 1})")
    }

    fun cancelCheckIn(habit: Habit, date: String) {
        val current = habit.checkInCounts[date] ?: 0
        val newCounts = when {
            current <= 1 -> habit.checkInCounts - date
            else -> habit.checkInCounts + (date to current - 1)
        }
        update(habit.copy(checkInCounts = newCounts))
        Timber.i("HabitStore cancelled check-in for habit: ${habit.name} on $date")
    }

    fun clearAll() {
        prefs.edit().remove(KEY_HABITS).apply()
        Timber.i("HabitStore cleared all data")
    }

    private fun save(habits: List<Habit>) {
        try {
            val currentJson = prefs.getString(KEY_HABITS, "[]") ?: "[]"
            val array = JSONArray()
            habits.forEach { array.put(HabitSerializer.toJson(it)) }
            prefs.edit()
                .putString(KEY_HABITS_BACKUP, currentJson)
                .putString(KEY_HABITS, array.toString())
                .apply()
        } catch (e: Exception) {
            Timber.e(e, "Failed to save habits")
        }
    }
}
