package org.wit.habit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.wit.habit.helpers.DateUtils
import org.wit.habit.helpers.HabitStore
import org.wit.habit.model.Habit
import timber.log.Timber

class MainActivity : AppCompatActivity(), HabitAdapter.OnHabitClickListener {

    private lateinit var habitStore: HabitStore
    private lateinit var recyclerView: RecyclerView
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var tvEmpty: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.i("Habit Tracker started")

        habitStore = HabitStore(this)

        recyclerView = findViewById(R.id.recyclerView)
        tvEmpty = findViewById(R.id.tvEmpty)

        habitAdapter = HabitAdapter(emptyList(), this)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = habitAdapter

        findViewById<Button>(R.id.btnAddHabit).setOnClickListener {
            startActivity(Intent(this, AddHabitActivity::class.java))
        }

        findViewById<Button>(R.id.btnStats).setOnClickListener {
            startActivity(Intent(this, StatsActivity::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        val habits = habitStore.findAll()
        if (habits.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            habitAdapter.updateData(habits)
        }
    }

    override fun onCheckInClick(habit: Habit) {
        val today = DateUtils.today()
        habitStore.checkIn(habit, today)
        refreshList()
    }

    override fun onCancelCheckInClick(habit: Habit) {
        val today = DateUtils.today()
        habitStore.cancelCheckIn(habit, today)
        refreshList()
    }

    override fun onEditClick(habit: Habit) {
        val intent = Intent(this, AddHabitActivity::class.java)
        intent.putExtra("habit_id", habit.id)
        startActivity(intent)
    }

    override fun onDeleteClick(habit: Habit) {
        habitStore.delete(habit)
        refreshList()
    }
}
