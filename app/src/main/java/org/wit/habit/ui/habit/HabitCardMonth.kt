package org.wit.habit.ui.habit

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
import org.wit.habit.util.DateUtils
import org.wit.habit.models.Habit
import org.wit.habit.ui.theme.HabitColorHelper
import org.wit.habit.ui.theme.HabitTheme

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitCardMonth(
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
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = habit.icon,
                    fontSize = 20.sp
                )
                Spacer(modifier = Modifier.weight(1f))
                Text(
                    text = habit.name,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Current-month calendar grid (Monday-first)
            val year = DateUtils.currentYear()
            val month = DateUtils.currentMonth()
            val daysInMonth = DateUtils.daysInMonth(year, month)
            val leadingBlanks = DateUtils.firstWeekdayOfMonth(year, month)
            val today = DateUtils.today()
            val cellSpacing = 4.dp
            val weekHeaders = listOf("M", "T", "W", "T", "F", "S", "S")

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Weekday header row
                Row(horizontalArrangement = Arrangement.spacedBy(cellSpacing)) {
                    weekHeaders.forEach { label ->
                        Text(
                            text = label,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.size(16.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Spacer(modifier = Modifier.height(4.dp))

                // Day cells: leading blanks + 1..daysInMonth, wrapped every 7
                val totalSlots = leadingBlanks + daysInMonth
                val rows = (totalSlots + 6) / 7
                for (row in 0 until rows) {
                    Row(horizontalArrangement = Arrangement.spacedBy(cellSpacing)) {
                        for (col in 0..6) {
                            val slot = row * 7 + col
                            if (slot < leadingBlanks || slot >= totalSlots) {
                                // Out-of-month placeholder, keeps columns aligned
                                androidx.compose.foundation.layout.Box(
                                    modifier = Modifier.size(16.dp)
                                )
                            } else {
                                val day = slot - leadingBlanks + 1
                                val dateStr = DateUtils.dateOfMonth(year, month, day)
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
                    if (row < rows - 1) {
                        Spacer(modifier = Modifier.height(cellSpacing))
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                CheckInButton(
                    isCompleted = isCompleted,
                    color = themeColor,
                    enabled = !isCompleted,
                    onClick = { onCheckIn(habit) },
                    modifier = Modifier
                        .weight(1f)
                        .height(40.dp),
                    iconSize = 18.dp,
                    fontSize = 14.sp
                )
                UndoCheckInButton(
                    onClick = { onCancelCheckIn(habit) },
                    enabled = count > 0,
                    modifier = Modifier
                        .size(40.dp)
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardMonthPreview() {
    HabitTheme {
        val year = DateUtils.currentYear()
        val month = DateUtils.currentMonth()
        val days = DateUtils.daysInMonth(year, month)
        val counts = mutableMapOf<String, Int>()
        for (day in 1..days) {
            // Spread a mix of empty / partial / complete across the month
            counts[DateUtils.dateOfMonth(year, month, day)] = when (day % 5) {
                0 -> 1
                1, 2 -> 0
                else -> 1
            }
        }
        HabitCardMonth(
            habit = Habit(
                id = 1,
                name = "Meditation",
                icon = "🧘",
                color = "purple",
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
