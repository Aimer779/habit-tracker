package org.wit.habit.ui.compose

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wit.habit.helpers.DateUtils
import org.wit.habit.model.Habit
import org.wit.habit.ui.theme.HabitTheme

@Composable
fun HabitCardWeek(
    habit: Habit,
    onCheckIn: (Habit) -> Unit,
    onCancelCheckIn: (Habit) -> Unit,
    modifier: Modifier = Modifier
) {
    val today = DateUtils.today()
    val count = habit.checkInCounts[today] ?: 0
    val isCompleted = count >= habit.targetCount
    val themeColor = HabitColorHelper.getColor(habit.color)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = Color.White)
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

            Text(
                text = habit.name,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(modifier = Modifier.width(8.dp))

            Row(
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                modifier = Modifier.padding(end = 8.dp)
            ) {
                for (i in 0..6) {
                    val dateStr = DateUtils.daysAgo(6 - i)
                    val dayCount = habit.checkInCounts[dateStr] ?: 0
                    val dayCompleted = dayCount >= habit.targetCount

                    HeatmapDot(
                        isCompleted = dayCompleted,
                        color = themeColor,
                        modifier = Modifier.padding(2.dp)
                    )
                }
            }

            Button(
                onClick = {
                    if (isCompleted) {
                        onCancelCheckIn(habit)
                    } else {
                        onCheckIn(habit)
                    }
                },
                modifier = Modifier
                    .width(100.dp)
                    .height(36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCompleted) Color(0xFFF44336) else themeColor
                ),
                shape = MaterialTheme.shapes.large,
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 0.dp)
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.AutoMirrored.Filled.Undo else Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(2.dp))
                Text(
                    text = if (isCompleted) "Cancel" else "Check",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
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
            onCancelCheckIn = {}
        )
    }
}
