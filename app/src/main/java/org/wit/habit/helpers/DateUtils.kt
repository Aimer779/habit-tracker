package org.wit.habit.helpers

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

object DateUtils {
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun today(): String = formatter.format(Date())

    fun daysAgo(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, -days)
        return formatter.format(cal.time)
    }

    fun getFirstDayOfMonth(year: Int, month: Int): String {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1)
        return formatter.format(cal.time)
    }

    fun getLastDayOfMonth(year: Int, month: Int): String {
        val cal = Calendar.getInstance()
        cal.set(year, month - 1, 1)
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        return formatter.format(cal.time)
    }

    fun isAfterMonth(year1: Int, month1: Int, year2: Int, month2: Int): Boolean {
        return year1 > year2 || (year1 == year2 && month1 > month2)
    }

    fun getWeekStart(dateStr: String): String {
        val cal = Calendar.getInstance()
        cal.time = formatter.parse(dateStr) ?: Date()
        val dayOfWeek = cal.get(Calendar.DAY_OF_WEEK)
        val diff = if (dayOfWeek == Calendar.SUNDAY) -6 else -(dayOfWeek - Calendar.MONDAY)
        cal.add(Calendar.DAY_OF_YEAR, diff)
        return formatter.format(cal.time)
    }

    fun addDays(dateStr: String, days: Int): String {
        val cal = Calendar.getInstance()
        cal.time = formatter.parse(dateStr) ?: Date()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return formatter.format(cal.time)
    }
}
