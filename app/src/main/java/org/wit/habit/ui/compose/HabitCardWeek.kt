package org.wit.habit.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wit.habit.helpers.DateUtils
import org.wit.habit.model.Habit
import org.wit.habit.ui.theme.HabitTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitCardWeek(
    habit: Habit,
    onCheckIn: (Habit) -> Unit,
    onCancelCheckIn: (Habit) -> Unit,
    onClick: (Habit) -> Unit,
    onLongClick: (Habit) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = DateUtils.today()
    val count = habit.checkInCounts[today] ?: 0
    val isCompleted = count >= habit.targetCount
    val themeColor = HabitColorHelper.getColor(habit.color)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp)
            .combinedClickable(
                onClick = { onClick(habit) },
                onLongClick = { onLongClick(habit) }
            ),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = habit.icon,
                fontSize = 20.sp
            )

            Spacer(modifier = Modifier.width(8.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = habit.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                if (habit.targetCount > 1) {
                    Text(
                        text = "Today: $count/${habit.targetCount}",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 1
                    )
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            // Current week (Mon-Sun) heatmap with weekday labels above the cells
            val weekStart = DateUtils.getWeekStart(DateUtils.today())
            val today = DateUtils.today()
            val weekLabels = listOf("M", "T", "W", "T", "F", "S", "S")
            val cellSpacing = 4.dp

            Column(verticalArrangement = Arrangement.spacedBy(cellSpacing)) {
                Row(horizontalArrangement = Arrangement.spacedBy(cellSpacing)) {
                    weekLabels.forEach { label ->
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }
                Row(horizontalArrangement = Arrangement.spacedBy(cellSpacing)) {
                    for (i in 0..6) {
                        val dateStr = DateUtils.addDays(weekStart, i)
                        val dayCount = habit.checkInCounts[dateStr] ?: 0

                        HeatmapCell(
                            level = heatLevel(dayCount, habit.targetCount),
                            color = themeColor,
                            isToday = dateStr == today,
                            contentDescription = heatmapCellContentDescription(
                                date = dateStr,
                                count = dayCount,
                                targetCount = habit.targetCount
                            )
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(8.dp))

            CheckInButton(
                isCompleted = isCompleted,
                color = themeColor,
                enabled = !isCompleted,
                onClick = { onCheckIn(habit) },
                modifier = Modifier
                    .width(104.dp)
                    .height(48.dp),
                iconSize = 16.dp,
                fontSize = 12.sp,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            UndoCheckInButton(
                onClick = { onCancelCheckIn(habit) },
                enabled = count > 0,
                modifier = Modifier
                    .size(48.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardWeekPreview() {
    HabitTheme {
        val weekStart = DateUtils.getWeekStart(DateUtils.today())
        val counts = mutableMapOf<String, Int>()
        for (i in 0..6) {
            counts[DateUtils.addDays(weekStart, i)] = if (i % 2 == 0) 1 else 0
        }
        HabitCardWeek(
            habit = Habit(
                id = 1,
                name = "Exercise",
                icon = "🏃",
                color = "green",
                targetCount = 1,
                checkInCounts = counts
            ),
            onCheckIn = {},
            onCancelCheckIn = {},
            onClick = {},
            onLongClick = {}
        )
    }
}
