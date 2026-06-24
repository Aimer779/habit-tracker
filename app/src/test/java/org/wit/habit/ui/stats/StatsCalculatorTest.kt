package org.wit.habit.ui.stats

import org.junit.Assert.assertEquals
import org.junit.Test
import org.wit.habit.models.Habit

class StatsCalculatorTest {

    @Test
    fun `calculate with empty habits returns zero summary`() {
        val summary = StatsCalculator.calculate(
            habits = emptyList(),
            period = StatsPeriod.Month(2026, 6),
            today = "2026-06-24"
        )

        assertEquals(0, summary.totalCheckIns)
        assertEquals(0, summary.activeDays)
        assertEquals(0, summary.currentStreak)
        assertEquals(0, summary.longestStreak)
        assertEquals(emptyList<RankItem>(), summary.rankings)
        assertEquals(0, summary.maxCount)
    }

    @Test
    fun `month stats counts check-ins within month only`() {
        val habits = listOf(
            Habit(
                id = 1L,
                name = "Read",
                targetCount = 1,
                checkInCounts = mapOf(
                    "2026-05-31" to 1,
                    "2026-06-01" to 1,
                    "2026-06-15" to 2,
                    "2026-07-01" to 1
                )
            )
        )

        val summary = StatsCalculator.calculate(
            habits = habits,
            period = StatsPeriod.Month(2026, 6),
            today = "2026-06-24"
        )

        assertEquals(3, summary.totalCheckIns)
        assertEquals(2, summary.activeDays)
        assertEquals(2, summary.rankings.first().count)
    }

    @Test
    fun `week stats counts check-ins within week only`() {
        val habits = listOf(
            Habit(
                id = 1L,
                name = "Exercise",
                targetCount = 1,
                checkInCounts = mapOf(
                    "2026-06-21" to 1,
                    "2026-06-22" to 1,
                    "2026-06-28" to 1,
                    "2026-06-29" to 1
                )
            )
        )

        val summary = StatsCalculator.calculate(
            habits = habits,
            period = StatsPeriod.Week("2026-06-22"),
            today = "2026-06-24"
        )

        assertEquals(2, summary.totalCheckIns)
        assertEquals(2, summary.activeDays)
        assertEquals(2, summary.rankings.first().count)
    }

    @Test
    fun `year stats counts check-ins within year only`() {
        val habits = listOf(
            Habit(
                id = 1L,
                name = "Meditate",
                targetCount = 1,
                checkInCounts = mapOf(
                    "2025-12-31" to 1,
                    "2026-01-01" to 1,
                    "2026-12-31" to 1,
                    "2027-01-01" to 1
                )
            )
        )

        val summary = StatsCalculator.calculate(
            habits = habits,
            period = StatsPeriod.Year(2026),
            today = "2026-06-24"
        )

        assertEquals(2, summary.totalCheckIns)
        assertEquals(2, summary.activeDays)
        assertEquals(2, summary.rankings.first().count)
    }

    @Test
    fun `rankings are sorted by count desc then name asc`() {
        val habits = listOf(
            Habit(
                id = 1L,
                name = "Bob",
                targetCount = 1,
                checkInCounts = mapOf("2026-06-24" to 1)
            ),
            Habit(
                id = 2L,
                name = "Alice",
                targetCount = 1,
                checkInCounts = mapOf("2026-06-23" to 1, "2026-06-24" to 1)
            ),
            Habit(
                id = 3L,
                name = "Carol",
                targetCount = 1,
                checkInCounts = mapOf("2026-06-22" to 1, "2026-06-24" to 1)
            ),
            Habit(id = 4L, name = "Dave", targetCount = 1, checkInCounts = emptyMap())
        )

        val summary = StatsCalculator.calculate(
            habits = habits,
            period = StatsPeriod.Month(2026, 6),
            today = "2026-06-24"
        )

        assertEquals(listOf("Alice", "Carol", "Bob"), summary.rankings.map { it.name })
        assertEquals(2, summary.rankings.first().count)
        assertEquals(1, summary.rankings.last().count)
        assertEquals(3, summary.rankings.size)
    }

    @Test
    fun `habit with check-ins below target does not contribute to ranking count`() {
        val habits = listOf(
            Habit(
                id = 1L,
                name = "Water",
                targetCount = 3,
                checkInCounts = mapOf("2026-06-24" to 2)
            )
        )

        val summary = StatsCalculator.calculate(
            habits = habits,
            period = StatsPeriod.Month(2026, 6),
            today = "2026-06-24"
        )

        assertEquals(0, summary.rankings.size)
    }

    @Test
    fun `current streak is zero when no check-ins`() {
        val habits = listOf(
            Habit(id = 1L, name = "Read", targetCount = 1, checkInCounts = emptyMap())
        )

        val streak = StatsCalculator.calculateAnyHabitCurrentStreak(habits, "2026-06-24")

        assertEquals(0, streak)
    }

    @Test
    fun `current streak is zero when today has no check-in`() {
        val habits = listOf(
            Habit(
                id = 1L,
                name = "Read",
                targetCount = 1,
                checkInCounts = mapOf("2026-06-23" to 1)
            )
        )

        val streak = StatsCalculator.calculateAnyHabitCurrentStreak(habits, "2026-06-24")

        assertEquals(0, streak)
    }

    @Test
    fun `current streak spans across months`() {
        val habits = listOf(
            Habit(
                id = 1L,
                name = "Read",
                targetCount = 1,
                checkInCounts = mapOf(
                    "2026-05-30" to 1,
                    "2026-05-31" to 1,
                    "2026-06-01" to 1,
                    "2026-06-02" to 1
                )
            )
        )

        val streak = StatsCalculator.calculateAnyHabitCurrentStreak(habits, "2026-06-02")

        assertEquals(4, streak)
    }

    @Test
    fun `current streak stops at gap`() {
        val habits = listOf(
            Habit(
                id = 1L,
                name = "Read",
                targetCount = 1,
                checkInCounts = mapOf(
                    "2026-06-20" to 1,
                    "2026-06-21" to 1,
                    "2026-06-22" to 1,
                    "2026-06-24" to 1
                )
            )
        )

        val streak = StatsCalculator.calculateAnyHabitCurrentStreak(habits, "2026-06-24")

        assertEquals(1, streak)
    }

    @Test
    fun `longest streak is zero with no active days`() {
        val streak = StatsCalculator.calculateLongestStreak(emptySet())

        assertEquals(0, streak)
    }

    @Test
    fun `longest streak with single active day is one`() {
        val streak = StatsCalculator.calculateLongestStreak(setOf("2026-06-24"))

        assertEquals(1, streak)
    }

    @Test
    fun `longest streak with gap in middle`() {
        val activeDays = setOf(
            "2026-06-20",
            "2026-06-21",
            "2026-06-22",
            "2026-06-24",
            "2026-06-25",
            "2026-06-26"
        )

        val streak = StatsCalculator.calculateLongestStreak(activeDays)

        assertEquals(3, streak)
    }

    @Test
    fun `longest streak spans across months`() {
        val activeDays = setOf(
            "2026-05-30",
            "2026-05-31",
            "2026-06-01",
            "2026-06-02"
        )

        val streak = StatsCalculator.calculateLongestStreak(activeDays)

        assertEquals(4, streak)
    }

    @Test
    fun `zero count dates are ignored for active days and streaks`() {
        val habits = listOf(
            Habit(
                id = 1L,
                name = "Read",
                targetCount = 1,
                checkInCounts = mapOf(
                    "2026-06-23" to 1,
                    "2026-06-24" to 0,
                    "2026-06-25" to 1
                )
            )
        )

        val summary = StatsCalculator.calculate(
            habits = habits,
            period = StatsPeriod.Month(2026, 6),
            today = "2026-06-25"
        )

        assertEquals(2, summary.totalCheckIns)
        assertEquals(2, summary.activeDays)
        assertEquals(1, summary.currentStreak)
        assertEquals(1, summary.longestStreak)
    }
}
