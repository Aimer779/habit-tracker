package org.wit.habit.ui.stats

import org.wit.habit.models.Habit
import org.wit.habit.utils.DateUtils
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

sealed class StatsPeriod {
    data class Week(val startDate: String) : StatsPeriod()
    data class Month(val year: Int, val month: Int) : StatsPeriod()
    data class Year(val year: Int) : StatsPeriod()
}

data class StatsSummary(
    val totalCheckIns: Int,
    val activeDays: Int,
    val currentStreak: Int,
    val longestStreak: Int,
    val rankings: List<RankItem>,
    val maxCount: Int
)

object StatsCalculator {

    fun calculate(habits: List<Habit>, period: StatsPeriod, today: String): StatsSummary {
        val activeDaysSet = mutableSetOf<String>()
        val habitCounts = mutableMapOf<Long, Int>()
        var totalCheckIns = 0

        habits.forEach { habit ->
            var count = 0
            habit.checkInCounts.forEach { (date, checkInCount) ->
                if (isInPeriod(date, period)) {
                    totalCheckIns += checkInCount
                    if (checkInCount > 0) {
                        activeDaysSet.add(date)
                    }
                    if (checkInCount >= habit.targetCount) {
                        count++
                    }
                }
            }
            habitCounts[habit.id] = count
        }

        val rankings = buildRankings(habits, habitCounts)
        val maxCount = rankings.maxOfOrNull { it.count } ?: 0

        return StatsSummary(
            totalCheckIns = totalCheckIns,
            activeDays = activeDaysSet.size,
            currentStreak = calculateAnyHabitCurrentStreak(habits, today),
            longestStreak = calculateLongestStreak(activeDaysSet),
            rankings = rankings,
            maxCount = maxCount
        )
    }

    private fun isInPeriod(date: String, period: StatsPeriod): Boolean {
        return when (period) {
            is StatsPeriod.Week -> {
                val end = DateUtils.addDays(period.startDate, 6)
                date in period.startDate..end
            }
            is StatsPeriod.Month -> {
                val firstDay = DateUtils.getFirstDayOfMonth(period.year, period.month)
                val lastDay = DateUtils.getLastDayOfMonth(period.year, period.month)
                date in firstDay..lastDay
            }
            is StatsPeriod.Year -> date.startsWith("${period.year}-")
        }
    }

    private fun buildRankings(habits: List<Habit>, habitCounts: Map<Long, Int>): List<RankItem> {
        return habits
            .filter { (habitCounts[it.id] ?: 0) > 0 }
            .map {
                RankItem(
                    icon = it.icon,
                    name = it.name,
                    count = habitCounts[it.id] ?: 0
                )
            }
            .sortedWith(compareByDescending<RankItem> { it.count }.thenBy { it.name })
    }

    fun calculateAnyHabitCurrentStreak(habits: List<Habit>, today: String): Int {
        val allDates = mutableSetOf<String>()
        habits.forEach { habit ->
            habit.checkInCounts.forEach { (date, count) ->
                if (count > 0) {
                    allDates.add(date)
                }
            }
        }

        if (allDates.isEmpty()) return 0

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val todayDate = formatter.parse(today) ?: return 0

        var streak = 0
        cal.time = todayDate

        while (true) {
            val dateStr = formatter.format(cal.time)
            if (allDates.contains(dateStr)) {
                streak++
            } else {
                if (streak == 0 && dateStr == today) {
                    return 0
                }
                break
            }
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        return streak
    }

    fun calculateLongestStreak(activeDates: Set<String>): Int {
        if (activeDates.isEmpty()) return 0

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sortedDates = activeDates.mapNotNull { formatter.parse(it) }.sorted()

        if (sortedDates.isEmpty()) return 0

        var maxStreak = 1
        var currentStreak = 1
        val cal = Calendar.getInstance()

        for (i in 1 until sortedDates.size) {
            val prev = sortedDates[i - 1]
            val curr = sortedDates[i]

            cal.time = prev
            cal.add(Calendar.DAY_OF_YEAR, 1)

            if (formatter.format(cal.time) == formatter.format(curr)) {
                currentStreak++
                maxStreak = maxOf(maxStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }

        return maxStreak
    }
}
