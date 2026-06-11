package org.wit.habit.ui.compose

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.wit.habit.ui.theme.HabitTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ViewModeSelector(
    selectedViewMode: ViewMode,
    onViewModeSelected: (ViewMode) -> Unit,
    modifier: Modifier = Modifier
) {
    SingleChoiceSegmentedButtonRow(modifier = modifier) {
        ViewMode.entries.forEachIndexed { index, viewMode ->
            SegmentedButton(
                selected = selectedViewMode == viewMode,
                onClick = { onViewModeSelected(viewMode) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = index,
                    count = ViewMode.entries.size
                )
            ) {
                Text(
                    text = viewMode.label,
                    style = MaterialTheme.typography.labelMedium
                )
            }
        }
    }
}

@Composable
fun SortOrderButton(
    isAscending: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TextButton(
        onClick = onClick,
        modifier = modifier.height(40.dp)
    ) {
        Icon(
            imageVector = if (isAscending) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward,
            contentDescription = if (isAscending) "Sort ascending" else "Sort descending"
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = if (isAscending) "A-Z" else "Z-A",
            fontWeight = FontWeight.Medium
        )
    }
}

val ViewMode.label: String
    get() = when (this) {
        ViewMode.MONTH -> "Month"
        ViewMode.WEEK -> "Week"
        ViewMode.DAY -> "Day"
    }

@Preview(showBackground = true)
@Composable
fun HomeControlsPreview() {
    HabitTheme {
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ViewModeSelector(
                selectedViewMode = ViewMode.MONTH,
                onViewModeSelected = {}
            )
            SortOrderButton(
                isAscending = true,
                onClick = {}
            )
        }
    }
}
