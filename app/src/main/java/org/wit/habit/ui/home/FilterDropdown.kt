package org.wit.habit.ui.home

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import org.wit.habit.ui.theme.HabitTheme

enum class FilterOption(val displayName: String) {
    ALL("All"),
    CHECKED_IN("Checked In"),
    NOT_CHECKED_IN("Not Checked In")
}

@Composable
fun FilterDropdown(
    selectedFilter: FilterOption,
    onFilterSelected: (FilterOption) -> Unit,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val hasActiveFilter = selectedFilter != FilterOption.ALL

    Box(modifier = modifier) {
        FilterChip(
            selected = hasActiveFilter,
            onClick = { expanded = true },
            label = {
                Text(
                    text = selectedFilter.displayName,
                    style = MaterialTheme.typography.labelMedium
                )
            },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.size(FilterChipDefaults.IconSize)
                )
            },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedLeadingIconColor = MaterialTheme.colorScheme.onPrimaryContainer,
                selectedTrailingIconColor = MaterialTheme.colorScheme.onPrimaryContainer
            ),
            modifier = Modifier
                .height(40.dp)
                .semantics {
                    contentDescription = "Filter habits, current filter ${selectedFilter.displayName}"
                }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            offset = DpOffset(x = 8.dp, y = 96.dp),
            modifier = Modifier.width(180.dp)
        ) {
            FilterOption.entries.forEach { option ->
                val selected = option == selectedFilter

                DropdownMenuItem(
                    text = {
                        Text(
                            text = option.displayName,
                            style = MaterialTheme.typography.bodyMedium,
                            color = if (selected) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    },
                    onClick = {
                        onFilterSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FilterDropdownPreview() {
    HabitTheme {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            FilterDropdown(
                selectedFilter = FilterOption.ALL,
                onFilterSelected = {}
            )
            FilterDropdown(
                selectedFilter = FilterOption.CHECKED_IN,
                onFilterSelected = {}
            )
        }
    }
}
