package org.wit.habit.ui.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.compose.ui.platform.ViewCompositionStrategy
import androidx.compose.ui.unit.dp
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import org.wit.habit.R
import org.wit.habit.utils.DateUtils
import org.wit.habit.utils.applySystemBarInsets
import org.wit.habit.data.local.HabitStore
import org.wit.habit.models.Habit
import org.wit.habit.ui.add.AddHabitActivity
import org.wit.habit.ui.home.FilterDropdown
import org.wit.habit.ui.home.FilterOption
import org.wit.habit.ui.home.MainContent
import org.wit.habit.ui.home.SortOrderButton
import org.wit.habit.ui.home.ViewModeSelector
import org.wit.habit.utils.ViewMode
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
        view.applySystemBarInsets()

        val composeView = view.findViewById<ComposeView>(R.id.composeView)
        composeView.setViewCompositionStrategy(
            ViewCompositionStrategy.DisposeOnViewTreeLifecycleDestroyed
        )
        composeView.setContent {
            HabitTheme {
                val allHabits = remember(refreshTrigger) {
                    habitStore.findAll()
                }
                val habits = remember(refreshTrigger, currentFilter, isAscending) {
                    getFilteredAndSortedHabits()
                }

                Column(modifier = Modifier.fillMaxSize()) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        FilterDropdown(
                            selectedFilter = currentFilter,
                            onFilterSelected = { filter ->
                                currentFilter = filter
                                Timber.i("User selected filter: $filter")
                            }
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        SortOrderButton(
                            isAscending = isAscending,
                            onClick = {
                                isAscending = !isAscending
                                Timber.i("User toggled sort order: ascending=$isAscending")
                            }
                        )
                    }

                    ViewModeSelector(
                        selectedViewMode = currentViewMode,
                        onViewModeSelected = { viewMode ->
                            currentViewMode = viewMode
                            Timber.i("User switched view mode to: $currentViewMode")
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 8.dp)
                    )

                    MainContent(
                        habits = habits,
                        viewMode = currentViewMode,
                        hasAnyHabits = allHabits.isNotEmpty(),
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
                            confirmDeleteHabit(habit)
                        },
                        modifier = Modifier.weight(1f),
                        contentPadding = PaddingValues(
                            start = 8.dp,
                            top = 8.dp,
                            end = 8.dp,
                            bottom = 8.dp
                        )
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

    private fun confirmDeleteHabit(habit: Habit) {
        val totalCheckIns = habit.checkInCounts.values.sum()
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Delete ${habit.name}?")
            .setMessage("This habit has $totalCheckIns check-ins. You can undo immediately after deleting.")
            .setPositiveButton("Delete") { _, _ ->
                deleteHabitWithUndo(habit)
            }
            .setNegativeButton("Cancel", null)
            .show()
        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(requireContext().getColor(com.google.android.material.R.color.design_default_color_error))
    }

    private fun deleteHabitWithUndo(habit: Habit) {
        habitStore.delete(habit)
        Timber.i("User deleted habit: ${habit.name}")
        refreshTrigger++

        Snackbar.make(requireView(), "Deleted ${habit.name}", Snackbar.LENGTH_LONG)
            .setAction("Undo") {
                habitStore.create(habit)
                Timber.i("User restored deleted habit: ${habit.name}")
                refreshTrigger++
            }
            .show()
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
