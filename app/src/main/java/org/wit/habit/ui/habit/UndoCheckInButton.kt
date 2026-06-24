package org.wit.habit.ui.habit

import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import org.wit.habit.ui.theme.HabitTheme

/**
 * Independent "-1" undo button. Enabled whenever today's check-in count > 0,
 * so a stray Check tap on a multi-target habit (e.g. 1 -> 2) can always be undone.
 * Keeps undo decoupled from the completed state of [CheckInButton].
 */
@Composable
fun UndoCheckInButton(
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    iconSize: Dp = 18.dp
) {
    val haptics = LocalHapticFeedback.current
    OutlinedButton(
        onClick = {
            haptics.performHapticFeedback(HapticFeedbackType.LongPress)
            onClick()
        },
        modifier = modifier.semantics {
            this.contentDescription =
                if (enabled) "Undo last check-in" else "No check-in to undo"
        },
        enabled = enabled,
        shape = MaterialTheme.shapes.large,
        contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Remove,
            contentDescription = null,
            tint = if (enabled) MaterialTheme.colorScheme.onSurfaceVariant
            else Color.Unspecified,
            modifier = Modifier.size(iconSize)
        )
    }
}

@Preview(showBackground = true)
@Composable
fun UndoCheckInButtonPreview() {
    HabitTheme {
        androidx.compose.foundation.layout.Row(
            horizontalArrangement = androidx.compose.foundation.layout.Arrangement.spacedBy(8.dp),
            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
        ) {
            UndoCheckInButton(onClick = {}, enabled = true)
            Text("enabled", fontSize = 12.sp)
            UndoCheckInButton(onClick = {}, enabled = false)
            Text("disabled", fontSize = 12.sp)
        }
    }
}
