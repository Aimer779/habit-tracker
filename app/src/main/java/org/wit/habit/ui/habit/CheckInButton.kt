package org.wit.habit.ui.habit

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp

@Composable
fun CheckInButton(
    isCompleted: Boolean,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    showText: Boolean = true,
    iconSize: Dp = 18.dp,
    fontSize: TextUnit = TextUnit.Unspecified,
    contentPadding: PaddingValues = ButtonDefaults.ContentPadding
) {
    val haptics = LocalHapticFeedback.current
    val animatedColor by animateColorAsState(
        targetValue = color,
        animationSpec = tween(durationMillis = 180),
        label = "checkInButtonColor"
    )
    val clickWithFeedback = {
        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
        onClick()
    }

    if (isCompleted) {
        OutlinedButton(
            onClick = clickWithFeedback,
            modifier = modifier,
            enabled = enabled,
            colors = ButtonDefaults.outlinedButtonColors(
                contentColor = MaterialTheme.colorScheme.primary
            ),
            shape = MaterialTheme.shapes.large,
            contentPadding = contentPadding
        ) {
            CheckInButtonContent(
                isCompleted = true,
                showText = showText,
                iconSize = iconSize,
                fontSize = fontSize
            )
        }
    } else {
        Button(
            onClick = clickWithFeedback,
            modifier = modifier,
            enabled = enabled,
            colors = ButtonDefaults.buttonColors(
                containerColor = animatedColor,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ),
            shape = MaterialTheme.shapes.large,
            contentPadding = contentPadding
        ) {
            CheckInButtonContent(
                isCompleted = false,
                showText = showText,
                iconSize = iconSize,
                fontSize = fontSize
            )
        }
    }
}

@Composable
private fun CheckInButtonContent(
    isCompleted: Boolean,
    showText: Boolean,
    iconSize: Dp,
    fontSize: TextUnit
) {
    AnimatedContent(
        targetState = isCompleted,
        transitionSpec = {
            fadeIn(animationSpec = tween(120)) togetherWith fadeOut(animationSpec = tween(90))
        },
        label = "checkInButtonContent"
    ) { completed ->
        Row(verticalAlignment = Alignment.CenterVertically) {
            val icon = if (completed) Icons.Default.Check else Icons.Default.Add
            val label = if (completed) "Done" else "Check"

            Icon(
                imageVector = icon,
                contentDescription = label,
                modifier = Modifier.size(iconSize)
            )
            if (showText) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = label,
                    fontSize = fontSize,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
