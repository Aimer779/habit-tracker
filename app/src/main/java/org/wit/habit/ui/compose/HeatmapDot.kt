package org.wit.habit.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.wit.habit.ui.theme.HabitTheme

private val IncompleteColor = Color(0xFFBDBDBD)

@Composable
fun HeatmapDot(
    isCompleted: Boolean,
    color: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .size(18.dp)
            .background(
                color = if (isCompleted) color else IncompleteColor,
                shape = CircleShape
            )
    )
}

@Preview(showBackground = true)
@Composable
fun HeatmapDotPreview() {
    HabitTheme {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.background(Color.White)
        ) {
            HeatmapDot(isCompleted = true, color = Color(0xFF42A5F5))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
            HeatmapDot(isCompleted = false, color = Color(0xFF42A5F5))
        }
    }
}
