package org.wit.habit.ui.add

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.core.view.WindowCompat
import org.wit.habit.data.local.HabitStore
import org.wit.habit.models.Habit
import org.wit.habit.ui.base.BaseActivity
import org.wit.habit.ui.theme.HabitTheme
import timber.log.Timber

class AddHabitActivity : BaseActivity() {
    private lateinit var habitStore: HabitStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)

        habitStore = HabitStore(this)
        val habitId = intent.getLongExtra("habit_id", -1L)
        val existingHabit = if (habitId != -1L) habitStore.findById(habitId) else null

        setContent {
            HabitTheme {
                AddHabitScreen(
                    habit = existingHabit,
                    onNavigateBack = { finish() },
                    onSave = { habit ->
                        if (existingHabit != null) {
                            habitStore.update(habit)
                            Timber.i("User updated habit: ${habit.name}")
                        } else {
                            habitStore.create(habit)
                            Timber.i("User created habit: ${habit.name}")
                        }
                        finish()
                    }
                )
            }
        }
    }
}
