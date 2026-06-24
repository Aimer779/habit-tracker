package org.wit.habit.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.wit.habit.utils.DateUtils
import org.wit.habit.models.Habit
import org.wit.habit.ui.habit.HabitCard
import org.wit.habit.ui.habit.HabitCardCallbacks
import org.wit.habit.utils.ViewMode
import org.wit.habit.ui.theme.HabitTheme

@Composable
fun HabitList(
    habits: List<Habit>,
    viewMode: ViewMode,
    callbacks: HabitCardCallbacks,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp)
) {
    val columns = when (viewMode) {
        ViewMode.MONTH -> 2
        ViewMode.WEEK, ViewMode.DAY -> 1
    }

    LazyVerticalGrid(
        columns = GridCells.Fixed(columns),
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding
    ) {
        items(habits, key = { it.id }) { habit ->
            HabitCard(
                habit = habit,
                viewMode = viewMode,
                callbacks = callbacks,
                modifier = Modifier.animateItem()
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitListMonthPreview() {
    HabitTheme {
        HabitList(
            habits = listOf(
                Habit(
                    id = 1,
                    name = "Read Books",
                    icon = "📖",
                    color = "blue",
                    targetCount = 1,
                    checkInCounts = mapOf(DateUtils.today() to 1)
                ),
                Habit(
                    id = 2,
                    name = "Exercise",
                    icon = "🏃",
                    color = "green",
                    targetCount = 3,
                    checkInCounts = emptyMap()
                ),
                Habit(
                    id = 3,
                    name = "Meditation",
                    icon = "🧘",
                    color = "purple",
                    targetCount = 1,
                    checkInCounts = mapOf(DateUtils.today() to 1)
                )
            ),
            viewMode = ViewMode.MONTH,
            callbacks = HabitCardCallbacks(
                onCheckIn = {},
                onCancelCheckIn = {},
                onClick = {},
                onLongClick = {}
            )
        )
    }
}

@Preview(showBackground = true)
@Composable
fun HabitListDayPreview() {
    HabitTheme {
        HabitList(
            habits = listOf(
                Habit(
                    id = 1,
                    name = "Read Books",
                    icon = "📖",
                    color = "blue",
                    targetCount = 1,
                    checkInCounts = mapOf(DateUtils.today() to 1)
                ),
                Habit(
                    id = 2,
                    name = "Exercise",
                    icon = "🏃",
                    color = "green",
                    targetCount = 3,
                    checkInCounts = emptyMap()
                )
            ),
            viewMode = ViewMode.DAY,
            callbacks = HabitCardCallbacks(
                onCheckIn = {},
                onCancelCheckIn = {},
                onClick = {},
                onLongClick = {}
            )
        )
    }
}
