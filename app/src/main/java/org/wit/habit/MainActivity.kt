package org.wit.habit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.wit.habit.helpers.HabitStore
import org.wit.habit.model.Habit
import timber.log.Timber.i
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private lateinit var habitStore: HabitStore
    private lateinit var habitListContainer: LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        i("Habit Tracker started..")

        habitStore = HabitStore(this)
        habitListContainer = findViewById(R.id.habitListContainer)

        findViewById<Button>(R.id.btnAddHabit).setOnClickListener {
            startActivity(Intent(this, HabitActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        habitListContainer.removeAllViews()
        val habits = habitStore.findAll()
        if (habits.isEmpty()) {
            val emptyView = TextView(this)
            emptyView.text = "暂无习惯，点击下方按钮添加"
            emptyView.textSize = 16f
            emptyView.setPadding(32, 32, 32, 32)
            habitListContainer.addView(emptyView)
            return
        }
        habits.forEach { habit ->
            val itemView = LayoutInflater.from(this).inflate(R.layout.item_habit, habitListContainer, false)
            itemView.findViewById<TextView>(R.id.habitTitle).text = habit.title
            itemView.findViewById<TextView>(R.id.habitStreak).text = "连续打卡: ${calculateStreak(habit)} 天"
            val checkInBtn = itemView.findViewById<Button>(R.id.btnCheckIn)
            checkInBtn.text = if (isCheckedInToday(habit)) "已打卡" else "打卡"
            checkInBtn.isEnabled = !isCheckedInToday(habit)
            checkInBtn.setOnClickListener {
                checkIn(habit)
            }
            itemView.findViewById<Button>(R.id.btnDelete).setOnClickListener {
                deleteHabit(habit)
            }
            habitListContainer.addView(itemView)
        }
    }

    private fun checkIn(habit: Habit) {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        habitStore.checkIn(habit, today)
        refreshList()
    }

    private fun deleteHabit(habit: Habit) {
        habitStore.delete(habit)
        refreshList()
    }

    private fun isCheckedInToday(habit: Habit): Boolean {
        val today = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date())
        return habit.checkInDates.contains(today)
    }

    private fun calculateStreak(habit: Habit): Int {
        if (habit.checkInDates.isEmpty()) return 0
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val calendar = Calendar.getInstance()
        var streak = 0
        while (true) {
            val dateStr = sdf.format(calendar.time)
            if (habit.checkInDates.contains(dateStr)) {
                streak++
                calendar.add(Calendar.DAY_OF_YEAR, -1)
            } else {
                break
            }
        }
        return streak
    }
}
