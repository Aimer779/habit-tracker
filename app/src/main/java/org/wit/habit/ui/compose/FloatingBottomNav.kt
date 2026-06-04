package org.wit.habit.ui.compose

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import org.wit.habit.ui.theme.HabitTheme

enum class NavTab(val icon: ImageVector, val title: String) {
    HOME(Icons.Default.Home, "Home"),
    STATS(Icons.Default.BarChart, "Stats"),
    SETTINGS(Icons.Default.Settings, "Settings")
}

@Composable
fun FloatingBottomNav(
    selectedTab: NavTab,
    onTabSelected: (NavTab) -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            modifier = Modifier
                .height(64.dp)
                .widthIn(min = 240.dp),
            shape = RoundedCornerShape(32.dp),
            color = MaterialTheme.colorScheme.surface,
            shadowElevation = 8.dp,
            tonalElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(horizontal = 20.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                NavTab.entries.forEach { tab ->
                    val isSelected = tab == selectedTab

                    IconButton(
                        onClick = { onTabSelected(tab) },
                        modifier = Modifier
                            .size(52.dp)
                            .background(
                                color = if (isSelected)
                                    Color(0xFF4DB6AC).copy(alpha = 0.2f)
                                else Color.Transparent,
                                shape = CircleShape
                            )
                    ) {
                        Icon(
                            imageVector = tab.icon,
                            contentDescription = tab.title,
                            tint = if (isSelected)
                                Color(0xFF26A69A)
                            else Color(0xFF9E9E9E),
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun FloatingBottomNavPreview() {
    HabitTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
        ) {
            Spacer(modifier = Modifier.weight(1f))
            FloatingBottomNav(
                selectedTab = NavTab.HOME,
                onTabSelected = {}
            )
        }
    }
}
