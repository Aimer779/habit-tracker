package org.wit.habit

import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.wit.habit.helpers.DateUtils
import org.wit.habit.helpers.HabitStore

class StatsActivity : BaseActivity() {
    private lateinit var habitStore: HabitStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_stats)

        habitStore = HabitStore(this)

        val tvTotalHabits: TextView = findViewById(R.id.tvTotalHabits)
        val tvTodayCompleted: TextView = findViewById(R.id.tvTodayCompleted)
        val tvTodayRate: TextView = findViewById(R.id.tvTodayRate)
        val tvTotalCheckIns: TextView = findViewById(R.id.tvTotalCheckIns)

        val habits = habitStore.findAll()
        val today = DateUtils.today()

        val totalHabits = habits.size
        val todayCompleted = habits.count { (it.checkInCounts[today] ?: 0) >= it.targetCount }
        val todayRate = if (totalHabits > 0) {
            "${(todayCompleted * 100 / totalHabits)}%"
        } else {
            "0%"
        }
        val totalCheckIns = habits.sumOf { it.checkInCounts.values.sum() }

        tvTotalHabits.text = "Total Habits: $totalHabits"
        tvTodayCompleted.text = "Today Completed: $todayCompleted"
        tvTodayRate.text = "Today's Completion Rate: $todayRate"
        tvTotalCheckIns.text = "Total Check-ins: $totalCheckIns"

        findViewById<Button>(R.id.btnBack).setOnClickListener {
            finish()
        }
    }
}
