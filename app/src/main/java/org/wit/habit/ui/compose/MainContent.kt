package org.wit.habit.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import org.wit.habit.helpers.DateUtils
import org.wit.habit.model.Habit
import org.wit.habit.ui.theme.HabitTheme

@Composable
fun MainContent(
    habits: List<Habit>,
    viewMode: ViewMode,
    isEmpty: Boolean,
    onCheckIn: (Habit) -> Unit,
    onCancelCheckIn: (Habit) -> Unit,
    onEdit: (Habit) -> Unit,
    onDelete: (Habit) -> Unit,
    modifier: Modifier = Modifier
) {
    if (isEmpty) {
        EmptyState(modifier = modifier)
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
            modifier = modifier
        )
    }
}

@Composable
private fun EmptyState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "No habits yet. Tap the button below to add one.",
            fontSize = 16.sp,
            textAlign = TextAlign.Center
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
            isEmpty = true,
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
                    checkInCounts = mutableMapOf(DateUtils.today() to 1)
                )
            ),
            viewMode = ViewMode.MONTH,
            isEmpty = false,
            onCheckIn = {},
            onCancelCheckIn = {},
            onEdit = {},
            onDelete = {}
        )
    }
}
