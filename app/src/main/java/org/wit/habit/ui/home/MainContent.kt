package org.wit.habit.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wit.habit.utils.DateUtils
import org.wit.habit.models.Habit
import org.wit.habit.ui.habit.HabitCardCallbacks
import org.wit.habit.utils.ViewMode
import org.wit.habit.ui.theme.HabitTheme

@Composable
fun MainContent(
    habits: List<Habit>,
    viewMode: ViewMode,
    hasAnyHabits: Boolean,
    onCheckIn: (Habit) -> Unit,
    onCancelCheckIn: (Habit) -> Unit,
    onEdit: (Habit) -> Unit,
    onDelete: (Habit) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(8.dp)
) {
    if (habits.isEmpty()) {
        EmptyState(
            hasAnyHabits = hasAnyHabits,
            modifier = modifier
        )
    } else {
        HabitList(
            habits = habits,
            viewMode = viewMode,
            callbacks = HabitCardCallbacks(
                onCheckIn = onCheckIn,
                onCancelCheckIn = onCancelCheckIn,
                onClick = onEdit,
                onLongClick = onDelete
            ),
            modifier = modifier,
            contentPadding = contentPadding
        )
    }
}

@Composable
private fun EmptyState(
    hasAnyHabits: Boolean,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (hasAnyHabits) {
                "No habits match the current filter."
            } else {
                "No habits yet. Tap the button below to add one."
            },
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentEmptyPreview() {
    HabitTheme {
        MainContent(
            habits = emptyList(),
            viewMode = ViewMode.MONTH,
            hasAnyHabits = false,
            onCheckIn = {},
            onCancelCheckIn = {},
            onEdit = {},
            onDelete = {}
        )
    }
}

@Preview(showBackground = true)
@Composable
fun MainContentWithHabitsPreview() {
    HabitTheme {
        MainContent(
            habits = listOf(
                Habit(
                    id = 1,
                    name = "Read Books",
                    icon = "📖",
                    color = "blue",
                    targetCount = 1,
                    checkInCounts = mapOf(DateUtils.today() to 1)
                )
            ),
            viewMode = ViewMode.MONTH,
            hasAnyHabits = true,
            onCheckIn = {},
            onCancelCheckIn = {},
            onEdit = {},
            onDelete = {}
        )
    }
}
