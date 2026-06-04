package org.wit.habit

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageButton
import android.widget.TextView
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.unit.dp
import org.wit.habit.helpers.DateUtils
import org.wit.habit.helpers.HabitStore
import org.wit.habit.model.Habit
import org.wit.habit.ui.compose.*
import org.wit.habit.ui.theme.HabitTheme
import timber.log.Timber

class MainActivity : BaseActivity() {

    private lateinit var habitStore: HabitStore
    private lateinit var composeView: ComposeView
    private lateinit var tvEmpty: TextView

    private var currentFilter = FilterOption.ALL
    private var isAscending = true
    private var currentViewMode = ViewMode.MONTH
    private var currentTab = NavTab.HOME

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        Timber.i("Habit Tracker started")

        habitStore = HabitStore(this)

        composeView = findViewById(R.id.composeView)
        tvEmpty = findViewById(R.id.tvEmpty)

        // Setup Compose content with floating navigation and FAB
        setupComposeContent()

        findViewById<ImageButton>(R.id.btnCalendar).setOnClickListener {
            currentViewMode = when (currentViewMode) {
                ViewMode.MONTH -> ViewMode.WEEK
                ViewMode.WEEK -> ViewMode.DAY
                ViewMode.DAY -> ViewMode.MONTH
            }
            Timber.i("User switched view mode to: $currentViewMode")
            refreshComposeContent()
        }

        findViewById<ImageButton>(R.id.btnSort).setOnClickListener {
            isAscending = !isAscending
            Timber.i("User toggled sort order: ascending=$isAscending")
            refreshComposeContent()
        }
    }

    override fun onResume() {
        super.onResume()
        refreshComposeContent()
    }

    private fun setupComposeContent() {
        composeView.setContent {
            HabitTheme {
                var selectedTab by remember { mutableStateOf(currentTab) }
                var selectedFilter by remember { mutableStateOf(currentFilter) }
                val habits = getFilteredAndSortedHabits(selectedFilter)

                Box(modifier = Modifier.fillMaxSize()) {
                    // Main content
                    Column(modifier = Modifier.fillMaxSize()) {
                        // Filter dropdown in Compose
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 8.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start
                        ) {
                            FilterDropdown(
                                selectedFilter = selectedFilter,
                                onFilterSelected = { filter ->
                                    selectedFilter = filter
                                    currentFilter = filter
                                    Timber.i("User selected filter: $filter")
                                }
                            )
                        }

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
                                val intent = Intent(this@MainActivity, AddHabitActivity::class.java)
                                intent.putExtra("habit_id", habit.id)
                                startActivity(intent)
                            },
                            onDelete = { habit ->
                                habitStore.delete(habit)
                                Timber.i("User deleted habit: ${habit.name}")
                                refreshComposeContent()
                            },
                            modifier = Modifier
                                .weight(1f)
                                .padding(bottom = 100.dp) // Space for floating nav + FAB
                        )
                    }

                    // Navigation area at bottom (FAB + Nav bar)
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                    ) {
                        // Floating bottom navigation
                        FloatingBottomNav(
                            selectedTab = selectedTab,
                            onTabSelected = { tab ->
                                selectedTab = tab
                                currentTab = tab
                                handleTabNavigation(tab)
                            }
                        )

                        // FAB centered above navigation bar
                        FloatingActionButton(
                            onClick = {
                                startActivity(Intent(this@MainActivity, AddHabitActivity::class.java))
                            },
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .offset(y = (-48).dp), // Move FAB higher above nav bar
                            containerColor = Color(0xFF26A69A),
                            contentColor = Color.White,
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Habit"
                            )
                        }
                    }
                }
            }
        }

        // Update empty state visibility
        val habits = getFilteredAndSortedHabits()
        tvEmpty.visibility = if (habits.isEmpty()) View.VISIBLE else View.GONE
    }

    private fun handleTabNavigation(tab: NavTab) {
        when (tab) {
            NavTab.HOME -> {
                Timber.i("User selected Home tab")
                // Don't refresh - just stay on home, navigation already visible
            }
            NavTab.STATS -> {
                Timber.i("User navigated to Stats")
                startActivity(Intent(this, StatsActivity::class.java))
            }
            NavTab.SETTINGS -> {
                Timber.i("User navigated to Settings")
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
    }

    private fun getFilteredAndSortedHabits(filter: FilterOption = currentFilter): List<Habit> {
        val habits = habitStore.findAll()
        val today = DateUtils.today()

        val filtered = when (filter) {
            FilterOption.ALL -> habits
            FilterOption.CHECKED_IN -> habits.filter { (it.checkInCounts[today] ?: 0) >= it.targetCount }
            FilterOption.NOT_CHECKED_IN -> habits.filter { (it.checkInCounts[today] ?: 0) < it.targetCount }
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
