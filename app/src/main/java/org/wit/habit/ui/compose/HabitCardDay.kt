package org.wit.habit.ui.compose

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
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

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun HabitCardDay(
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
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Start
            ) {
                Text(
                    text = habit.icon,
                    fontSize = 24.sp
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = habit.name,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Today's Progress: $count/${habit.targetCount}",
                fontSize = 14.sp,
                color = Color.DarkGray
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (isCompleted) {
                        onCancelCheckIn(habit)
                    } else {
                        onCheckIn(habit)
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isCompleted) Color(0xFFF44336) else themeColor
                ),
                shape = MaterialTheme.shapes.large
            ) {
                Icon(
                    imageVector = if (isCompleted) Icons.AutoMirrored.Filled.Undo else Icons.Default.Add,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isCompleted) "Cancel" else "Check",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HabitCardDayPreview() {
    HabitTheme {
        HabitCardDay(
            habit = Habit(
                id = 1,
                name = "Read Books",
                icon = "📖",
                color = "blue",
                targetCount = 1,
                checkInCounts = mutableMapOf(DateUtils.today() to 1)
            ),
            onCheckIn = {},
            onCancelCheckIn = {},
            onClick = {},
            onLongClick = {}
        )
    }
}
