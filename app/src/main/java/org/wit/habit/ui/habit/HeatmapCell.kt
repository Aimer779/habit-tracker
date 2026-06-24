package org.wit.habit.ui.habit

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wit.habit.ui.theme.HabitTheme

/**
 * Tiered heatmap square (GitHub-style). Replaces the old continuous [HeatmapDot].
 *
 * Level mapping (by check-in progress vs target):
 *  - 0  empty      -> surfaceVariant
 *  - 1  partial    -> surfaceVariant blended toward the habit color
 *  - 2  complete   -> full habit color
 */
@Composable
fun HeatmapCell(
    level: Int,
    color: Color,
    modifier: Modifier = Modifier,
    isToday: Boolean = false,
    contentDescription: String? = null
) {
    val base = MaterialTheme.colorScheme.surfaceVariant
    val cellColor = when (level.coerceIn(0, 2)) {
        0 -> base
        1 -> lerp(base, color, 0.45f)
        else -> color
    }
    val shape = RoundedCornerShape(3.dp)
    val withToday = if (isToday) {
        modifier.border(1.dp, color, shape)
    } else {
        modifier
    }
    val semanticModifier = if (contentDescription != null) {
        withToday.semantics { this.contentDescription = contentDescription }
    } else {
        withToday
    }

    androidx.compose.foundation.layout.Box(
        modifier = semanticModifier
            .size(16.dp)
            .background(color = cellColor, shape = shape)
    )
}

/** 0 = empty / 1 = partial / 2 = complete, derived from check-in count vs target. */
internal fun heatLevel(count: Int, targetCount: Int): Int = when {
    count <= 0 -> 0
    count >= targetCount.coerceAtLeast(1) -> 2
    else -> 1
}

internal fun heatmapCellContentDescription(
    date: String,
    count: Int,
    targetCount: Int
): String {
    val safeTarget = targetCount.coerceAtLeast(1)
    val status = when {
        count >= safeTarget -> "complete"
        count > 0 -> "partial"
        else -> "not checked in"
    }
    val unit = if (safeTarget == 1) "check-in" else "check-ins"
    return "$date: $status, $count of $safeTarget $unit"
}

@Preview(showBackground = true)
@Composable
fun HeatmapCellPreview() {
    HabitTheme {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text("Empty", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            HeatmapCell(level = 0, color = Color(0xFF42A5F5))
            Spacer(Modifier.size(8.dp))
            Text("Partial", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            HeatmapCell(level = 1, color = Color(0xFF42A5F5))
            Spacer(Modifier.size(8.dp))
            Text("Complete", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            HeatmapCell(level = 2, color = Color(0xFF42A5F5))
            Spacer(Modifier.size(8.dp))
            Text("Today", fontSize = 12.sp, fontWeight = FontWeight.Bold)
            HeatmapCell(level = 2, color = Color(0xFF42A5F5), isToday = true)
        }
    }
}
