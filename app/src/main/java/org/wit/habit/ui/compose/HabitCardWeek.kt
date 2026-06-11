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

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                for (i in 0..6) {
                    val dateStr = DateUtils.daysAgo(6 - i)
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
                    .width(104.dp)
                    .height(48.dp),
                iconSize = 16.dp,
                fontSize = 12.sp,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardWeekPreview() {
    HabitTheme {
        HabitCardWeek(
            habit = Habit(
                id = 1,
                name = "Exercise",
                icon = "🏃",
                color = "green",
                targetCount = 1,
                checkInCounts = mutableMapOf(
                    DateUtils.daysAgo(0) to 1,
                    DateUtils.daysAgo(2) to 1,
                    DateUtils.daysAgo(4) to 1
                )
            ),
            onCheckIn = {},
            onCancelCheckIn = {},
            onClick = {},
            onLongClick = {}
        )
    }
}
