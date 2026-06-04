package org.wit.habit

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.wit.habit.helpers.DateUtils
import org.wit.habit.model.Habit
import org.wit.habit.ui.compose.*
import org.wit.habit.ui.theme.HabitTheme

/**
 * Compose preview activity for testing Compose setup and developing new components
 */
class ComposePreviewActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            HabitTheme {
                HabitComponentTestScreen()
            }
        }
    }
}

@Composable
fun HabitComponentTestScreen() {
    var selectedMode by remember { mutableStateOf(ViewMode.MONTH) }

    val testHabits = remember {
        mutableStateListOf(
            Habit(
                id = 1,
                name = "Read Books",
                icon = "📖",
                color = "blue",
                targetCount = 1,
                checkInCounts = mutableMapOf(
                    DateUtils.today() to 1,
                    DateUtils.daysAgo(1) to 1,
                    DateUtils.daysAgo(3) to 1,
                    DateUtils.daysAgo(7) to 1
                )
            ),
            Habit(
                id = 2,
                name = "Exercise",
                icon = "🏃",
                color = "green",
                targetCount = 3,
                checkInCounts = mutableMapOf(
                    DateUtils.daysAgo(0) to 2,
                    DateUtils.daysAgo(2) to 3,
                    DateUtils.daysAgo(5) to 1
                )
            ),
            Habit(
                id = 3,
                name = "Meditation",
                icon = "🧘",
                color = "purple",
                targetCount = 1,
                checkInCounts = mutableMapOf(
                    DateUtils.today() to 1,
                    DateUtils.daysAgo(2) to 1,
                    DateUtils.daysAgo(4) to 1
                )
            ),
            Habit(
                id = 4,
                name = "Drink Water",
                icon = "💧",
                color = "teal",
                targetCount = 8,
                checkInCounts = mutableMapOf(
                    DateUtils.today() to 5
                )
            )
        )
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Mode Selector
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                Button(
                    onClick = { selectedMode = ViewMode.MONTH },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMode == ViewMode.MONTH)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("Month")
                }
                Button(
                    onClick = { selectedMode = ViewMode.WEEK },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMode == ViewMode.WEEK)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("Week")
                }
                Button(
                    onClick = { selectedMode = ViewMode.DAY },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = if (selectedMode == ViewMode.DAY)
                            MaterialTheme.colorScheme.primary
                        else MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Text("Day")
                }
            }

            // Habit List
            HabitList(
                habits = testHabits,
                viewMode = selectedMode,
                callbacks = HabitCardCallbacks(
                    onCheckIn = { habit ->
                        timber.log.Timber.i("Check in: ${habit.name}")
                        val today = DateUtils.today()
                        val currentCount = habit.checkInCounts[today] ?: 0

                        // Find and update the habit in the list
                        val index = testHabits.indexOfFirst { it.id == habit.id }
                        if (index != -1) {
                            val updatedHabit = habit.copy(
                                checkInCounts = habit.checkInCounts.toMutableMap().apply {
                                    put(today, currentCount + 1)
                                }
                            )
                            testHabits[index] = updatedHabit
                        }
                    },
                    onCancelCheckIn = { habit ->
                        timber.log.Timber.i("Cancel check in: ${habit.name}")
                        val today = DateUtils.today()

                        // Find and update the habit in the list
                        val index = testHabits.indexOfFirst { it.id == habit.id }
                        if (index != -1) {
                            val updatedHabit = habit.copy(
                                checkInCounts = habit.checkInCounts.toMutableMap().apply {
                                    remove(today)
                                }
                            )
                            testHabits[index] = updatedHabit
                        }
                    },
                    onClick = { habit ->
                        timber.log.Timber.i("Clicked: ${habit.name}")
                    },
                    onLongClick = { habit ->
                        timber.log.Timber.i("Long clicked: ${habit.name}")
                    }
                )
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitComponentTestScreenPreview() {
    HabitTheme {
        HabitComponentTestScreen()
    }
}
