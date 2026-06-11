package org.wit.habit.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.wit.habit.ui.theme.HabitTheme

@Composable
fun HeatmapDot(
    progress: Float,
    color: Color,
    modifier: Modifier = Modifier,
    contentDescription: String? = null
) {
    val clampedProgress = progress.coerceIn(0f, 1f)
    val dotColor = if (clampedProgress <= 0f) {
        MaterialTheme.colorScheme.surfaceVariant
    } else {
        lerp(MaterialTheme.colorScheme.surfaceVariant, color, 0.35f + (0.65f * clampedProgress))
    }
    val semanticModifier = if (contentDescription != null) {
        modifier.semantics { this.contentDescription = contentDescription }
    } else {
        modifier
    }

    Box(
        modifier = semanticModifier
            .size(14.dp)
            .background(
                color = dotColor,
                shape = CircleShape
            )
    )
}

@Preview(showBackground = true)
@Composable
fun HeatmapDotPreview() {
    HabitTheme {
        androidx.compose.foundation.layout.Row(
            modifier = Modifier.background(MaterialTheme.colorScheme.background)
        ) {
            HeatmapDot(progress = 1f, color = Color(0xFF42A5F5))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
            HeatmapDot(progress = 0.4f, color = Color(0xFF42A5F5))
            androidx.compose.foundation.layout.Spacer(modifier = Modifier.size(4.dp))
            HeatmapDot(progress = 0f, color = Color(0xFF42A5F5))
        }
    }
}

internal fun heatmapDotContentDescription(
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
