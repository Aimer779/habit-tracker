package org.wit.habit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.PopupMenu
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.floatingactionbutton.FloatingActionButton
import org.wit.habit.helpers.DateUtils
import org.wit.habit.helpers.HabitStore
import org.wit.habit.model.Habit
import org.wit.habit.ui.compose.HabitCardCallbacks
import org.wit.habit.ui.compose.MainContent
import org.wit.habit.ui.compose.ViewMode
import org.wit.habit.ui.theme.HabitTheme
import timber.log.Timber

class MainActivity : BaseActivity() {

    private lateinit var habitStore: HabitStore
    private lateinit var composeView: ComposeView
    private lateinit var tvEmpty: TextView
    private lateinit var btnFilter: MaterialButton
    private lateinit var bottomNavigationView: BottomNavigationView

    private var currentFilter = Filter.ALL
    private var isAscending = true
    private var currentViewMode = ViewMode.MONTH

    enum class Filter { ALL, CHECKED_IN, NOT_CHECKED_IN }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.i("Habit Tracker started")

        habitStore = HabitStore(this)

        composeView = findViewById(R.id.composeView)
        tvEmpty = findViewById(R.id.tvEmpty)
        btnFilter = findViewById(R.id.btnFilter)
        bottomNavigationView = findViewById(R.id.bottomNavigationView)

        // Setup Compose content
        setupComposeContent()

        findViewById<FloatingActionButton>(R.id.fabAdd).setOnClickListener {
            startActivity(Intent(this, AddHabitActivity::class.java))
        }

        findViewById<ImageButton>(R.id.btnCalendar).setOnClickListener {
            currentViewMode = when (currentViewMode) {
                ViewMode.MONTH -> ViewMode.WEEK
                ViewMode.WEEK -> ViewMode.DAY
                ViewMode.DAY -> ViewMode.MONTH
            }
            Timber.i("User switched view mode to: $currentViewMode")
            refreshComposeContent()
        }

        btnFilter.setOnClickListener {
            showFilterPopup(it)
        }

        findViewById<ImageButton>(R.id.btnSort).setOnClickListener {
            isAscending = !isAscending
            Timber.i("User toggled sort order: ascending=$isAscending")
            refreshComposeContent()
        }

        bottomNavigationView.setOnItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_home -> {
                    refreshComposeContent()
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
                    Timber.i("User selected filter: ALL")
                }
                R.id.filter_checked_in -> {
                    currentFilter = Filter.CHECKED_IN
                    btnFilter.text = getString(R.string.checked_in)
                    Timber.i("User selected filter: CHECKED_IN")
                }
                R.id.filter_not_checked_in -> {
                    currentFilter = Filter.NOT_CHECKED_IN
                    btnFilter.text = getString(R.string.not_checked_in)
                    Timber.i("User selected filter: NOT_CHECKED_IN")
                }
            }
            refreshComposeContent()
            true
        }
        popup.show()
    }

    override fun onResume() {
        super.onResume()
        refreshComposeContent()
    }

    private fun setupComposeContent() {
        composeView.setContent {
            HabitTheme {
                val habits = getFilteredAndSortedHabits()

                MainContent(
                    habits = habits,
                    viewMode = currentViewMode,
                    isEmpty = habits.isEmpty(),
                    onCheckIn = { habit ->
                        val today = DateUtils.today()
                        habitStore.checkIn(habit, today)
                        Timber.i("User checked in habit: ${habit.name} on $today")
                        refreshComposeContent()
                    },
                    onCancelCheckIn = { habit ->
                        val today = DateUtils.today()
                        habitStore.cancelCheckIn(habit, today)
                        Timber.i("User cancelled check-in for habit: ${habit.name} on $today")
                        refreshComposeContent()
                    },
                    onEdit = { habit ->
                        val intent = Intent(this, AddHabitActivity::class.java)
                        intent.putExtra("habit_id", habit.id)
                        startActivity(intent)
                    },
                    onDelete = { habit ->
                        habitStore.delete(habit)
                        Timber.i("User deleted habit: ${habit.name}")
                        refreshComposeContent()
                    }
                )
            }
        }

        // Update empty state visibility
        val habits = getFilteredAndSortedHabits()
        tvEmpty.visibility = if (habits.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun getFilteredAndSortedHabits(): List<Habit> {
        val habits = habitStore.findAll()
        val today = DateUtils.today()

        val filtered = when (currentFilter) {
            Filter.ALL -> habits
            Filter.CHECKED_IN -> habits.filter { (it.checkInCounts[today] ?: 0) >= it.targetCount }
            Filter.NOT_CHECKED_IN -> habits.filter { (it.checkInCounts[today] ?: 0) < it.targetCount }
        }

        return if (isAscending) {
            filtered.sortedBy { it.name }
        } else {
            filtered.sortedByDescending { it.name }
        }
    }

    private fun refreshComposeContent() {
        setupComposeContent()
    }
}
