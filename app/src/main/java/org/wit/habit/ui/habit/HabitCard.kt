package org.wit.habit.ui.habit

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.wit.habit.models.Habit
import org.wit.habit.utils.ViewMode

data class HabitCardCallbacks(
    val onCheckIn: (Habit) -> Unit,
    val onCancelCheckIn: (Habit) -> Unit,
    val onClick: (Habit) -> Unit,
    val onLongClick: (Habit) -> Unit
)

@Composable
fun HabitCard(
    habit: Habit,
    viewMode: ViewMode,
    callbacks: HabitCardCallbacks,
    modifier: Modifier = Modifier
) {
    when (viewMode) {
        ViewMode.MONTH -> HabitCardMonth(
            habit = habit,
            onCheckIn = callbacks.onCheckIn,
            onCancelCheckIn = callbacks.onCancelCheckIn,
            onClick = callbacks.onClick,
            onLongClick = callbacks.onLongClick,
            modifier = modifier
        )
        ViewMode.WEEK -> HabitCardWeek(
            habit = habit,
            onCheckIn = callbacks.onCheckIn,
            onCancelCheckIn = callbacks.onCancelCheckIn,
            onClick = callbacks.onClick,
            onLongClick = callbacks.onLongClick,
            modifier = modifier
        )
        ViewMode.DAY -> HabitCardDay(
            habit = habit,
            onCheckIn = callbacks.onCheckIn,
            onCancelCheckIn = callbacks.onCancelCheckIn,
            onClick = callbacks.onClick,
            onLongClick = callbacks.onLongClick,
            modifier = modifier
        )
    }
}
