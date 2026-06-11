package org.wit.habit

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import org.wit.habit.helpers.DateUtils
import org.wit.habit.helpers.HabitStore
import org.wit.habit.model.Habit
import org.wit.habit.ui.compose.FilterDropdown
import org.wit.habit.ui.compose.FilterOption
import org.wit.habit.ui.compose.MainContent
import org.wit.habit.ui.compose.ViewMode
import org.wit.habit.ui.theme.HabitTheme
import timber.log.Timber

class HomeFragment : Fragment() {

    private lateinit var habitStore: HabitStore

    private var currentFilter by mutableStateOf(FilterOption.ALL)
    private var isAscending by mutableStateOf(true)
    private var currentViewMode by mutableStateOf(ViewMode.MONTH)
    private var refreshTrigger by mutableIntStateOf(0)

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        habitStore = HabitStore(requireContext())

        view.findViewById<ImageButton>(R.id.btnCalendar).setOnClickListener {
            currentViewMode = when (currentViewMode) {
                ViewMode.MONTH -> ViewMode.WEEK
                ViewMode.WEEK -> ViewMode.DAY
                ViewMode.DAY -> ViewMode.MONTH
            }
            Timber.i("User switched view mode to: $currentViewMode")
        }

        view.findViewById<ImageButton>(R.id.btnSort).setOnClickListener {
            isAscending = !isAscending
            Timber.i("User toggled sort order: ascending=$isAscending")
        }

        val composeView = view.findViewById<ComposeView>(R.id.composeView)
        composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
        composeView.setContent {
            HabitTheme {
                val habits = remember(refreshTrigger, currentFilter, isAscending) {
                    getFilteredAndSortedHabits()
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        FilterDropdown(
                            selectedFilter = currentFilter,
                            onFilterSelected = { filter ->
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
                            refreshTrigger++
                        },
                        onCancelCheckIn = { habit ->
                            val today = DateUtils.today()
                            habitStore.cancelCheckIn(habit, today)
                            Timber.i("User cancelled check-in for habit: ${habit.name} on $today")
                            refreshTrigger++
                        },
                        onEdit = { habit ->
                            val intent = Intent(requireContext(), AddHabitActivity::class.java)
                            intent.putExtra("habit_id", habit.id)
                            startActivity(intent)
                        },
                        onDelete = { habit ->
                            habitStore.delete(habit)
                            Timber.i("User deleted habit: ${habit.name}")
                            refreshTrigger++
                        },
                        modifier = Modifier
                            .weight(1f)
                            .padding(bottom = 100.dp) // Space for floating nav + FAB
                    )
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshTrigger++
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            refreshTrigger++
        }
    }

    private fun getFilteredAndSortedHabits(): List<Habit> {
        val habits = habitStore.findAll()
        val today = DateUtils.today()

        val filtered = when (currentFilter) {
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
}
