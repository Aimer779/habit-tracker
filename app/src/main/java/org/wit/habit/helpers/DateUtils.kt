package org.wit.habit.helpers

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object DateUtils {
    private val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    fun today(): String = formatter.format(Date())
}
