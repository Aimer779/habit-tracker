package org.wit.habit.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wit.habit.helpers.DateUtils
import org.wit.habit.model.Habit
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

            if (habit.targetCount > 1) {
                Text(
                    text = "Today: $count/${habit.targetCount}",
                    fontSize = 12.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
            }

            // Heatmap Grid: 7 columns x 5 rows = 35 dots (last 35 days)
            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                for (row in 0..4) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        for (col in 0..6) {
                            val dayIndex = 34 - (row * 7 + col)
                            val dateStr = DateUtils.daysAgo(dayIndex)
                            val dayCount = habit.checkInCounts[dateStr] ?: 0

                            HeatmapDot(
                                progress = dayCount.toFloat() / habit.targetCount.coerceAtLeast(1),
                                color = themeColor,
                                modifier = Modifier.padding(2.dp),
                                contentDescription = heatmapDotContentDescription(
                                    date = dateStr,
                                    count = dayCount,
                                    targetCount = habit.targetCount
                                )
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            CheckInButton(
                isCompleted = isCompleted,
                color = themeColor,
                onClick = {
                    if (isCompleted) {
                        onCancelCheckIn(habit)
                    } else {
                        onCheckIn(habit)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(40.dp),
                iconSize = 18.dp,
                fontSize = 14.sp
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardMonthPreview() {
    HabitTheme {
        HabitCardMonth(
            habit = Habit(
                id = 1,
                name = "Meditation",
                icon = "🧘",
                color = "purple",
                targetCount = 1,
                checkInCounts = mutableMapOf(
                    DateUtils.today() to 1,
                    DateUtils.daysAgo(1) to 1,
                    DateUtils.daysAgo(3) to 1,
                    DateUtils.daysAgo(7) to 1,
                    DateUtils.daysAgo(14) to 1
                )
            ),
            onCheckIn = {},
            onCancelCheckIn = {},
            onClick = {},
            onLongClick = {}
        )
    }
}
