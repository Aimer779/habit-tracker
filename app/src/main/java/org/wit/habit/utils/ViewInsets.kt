package org.wit.habit.utils

import android.view.View
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

/**
 * Applies system bar insets to this view's padding.
 *
 * Left and right are always adjusted to avoid status bar / gesture areas.
 * Top is adjusted by default; set [applyTop] to false if the view already has
 * its own top padding logic (e.g. a toolbar).
 * Bottom is left unchanged by default because the MainActivity bottom nav
 * already sets the fragment container's bottom padding; set [applyBottom] to
 * true when the view itself should consume the navigation bar inset.
 */
fun View.applySystemBarInsets(
    applyTop: Boolean = true,
    applyBottom: Boolean = false
) {
    val initialLeft = paddingLeft
    val initialTop = paddingTop
    val initialRight = paddingRight
    val initialBottom = paddingBottom

    ViewCompat.setOnApplyWindowInsetsListener(this) { target, insets ->
        val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
        target.setPadding(
            initialLeft + bars.left,
            initialTop + (if (applyTop) bars.top else 0),
            initialRight + bars.right,
            initialBottom + (if (applyBottom) bars.bottom else 0)
        )
        insets
    }
}
