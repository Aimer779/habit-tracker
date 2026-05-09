package org.wit.habit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.wit.habit.helpers.DateUtils
import org.wit.habit.helpers.HabitStore
import org.wit.habit.model.Habit
import timber.log.Timber

class MainActivity : BaseActivity(), HabitAdapter.OnHabitClickListener {

    private lateinit var habitStore: HabitStore
    private lateinit var recyclerView: RecyclerView
    private lateinit var habitAdapter: HabitAdapter
    private lateinit var tvEmpty: TextView
    private lateinit var btnFilter: MaterialButton
    private lateinit var bottomNavigationView: BottomNavigationView

    private var currentFilter = Filter.ALL
    private var isAscending = true

    enum class Filter { ALL, CHECKED_IN, NOT_CHECKED_IN }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.i("Habit Tracker started")

        habitStore = HabitStore(this)

        recyclerView = findViewById(R.id.recyclerView)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnFilter = findViewById(R.id.btnFilter)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        habitAdapter = HabitAdapter(emptyList(), this)
        recyclerView.layoutManager = GridLayoutManager(this, 2)
        recyclerView.adapter = habitAdapter

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, AddHabitActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btnCalendar).setOnClickListener {
            // 暂无日历功能
        }

        btnFilter.setOnClickListener {
            showFilterPopup(it)
        }

        findViewById<ImageButton>(R.id.btnSort).setOnClickListener {
            isAscending = !isAscending
            refreshList()
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    refreshList()
                    true
                }
                R.id.nav_stats -> {
                    startActivity(Intent(this, StatsActivity::class.java))
                    true
                }
                R.id.nav_settings -> {
                    startActivity(Intent(this, SettingsActivity::class.java))
                    true
                }
                else -> false
            }
        }
        bottomNavigationView.selectedItemId = R.id.nav_home
    }

    private fun showFilterPopup(anchor: View) {
        val popup = PopupMenu(this, anchor)
        popup.menuInflater.inflate(R.menu.filter_menu, popup.menu)
        popup.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.filter_all -> {
                    currentFilter = Filter.ALL
                    btnFilter.text = getString(R.string.all)
                }
                R.id.filter_checked_in -> {
                    currentFilter = Filter.CHECKED_IN
                    btnFilter.text = getString(R.string.checked_in)
                }
                R.id.filter_not_checked_in -> {
                    currentFilter = Filter.NOT_CHECKED_IN
                    btnFilter.text = getString(R.string.not_checked_in)
                }
            }
            refreshList()
            true
        }
        popup.show()
    }

    override fun onResume() {
        super.onResume()
        refreshList()
    }

    private fun refreshList() {
        val habits = habitStore.findAll()
        val today = DateUtils.today()

        val filtered = when (currentFilter) {
            Filter.ALL -> habits
            Filter.CHECKED_IN -> habits.filter { it.checkInDates.contains(today) }
            Filter.NOT_CHECKED_IN -> habits.filter { !it.checkInDates.contains(today) }
        }

        val sorted = if (isAscending) {
            filtered.sortedBy { it.name }
        } else {
            filtered.sortedByDescending { it.name }
        }

        if (sorted.isEmpty()) {
            tvEmpty.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
        } else {
            tvEmpty.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            habitAdapter.updateData(sorted)
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
