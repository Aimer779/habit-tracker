package org.wit.habit

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import org.wit.habit.helpers.HabitColors
import org.wit.habit.helpers.HabitStore
import org.wit.habit.model.Habit

class AddHabitActivity : BaseActivity() {
    private lateinit var habitStore: HabitStore
    private lateinit var editName: EditText
    private lateinit var editDescription: EditText
    private lateinit var tvSelectedIcon: TextView
    private lateinit var viewSelectedColor: View
    private var existingHabit: Habit? = null

    private var selectedIcon: String = "✅"
    private var selectedColor: String = "blue"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)

        habitStore = HabitStore(this)
        editName = findViewById(R.id.editName)
        editDescription = findViewById(R.id.editDescription)
        tvSelectedIcon = findViewById(R.id.tvSelectedIcon)
        viewSelectedColor = findViewById(R.id.viewSelectedColor)

        val habitId = intent.getLongExtra("habit_id", -1L)
        if (habitId != -1L) {
            existingHabit = habitStore.findById(habitId)
            existingHabit?.let {
                editName.setText(it.name)
                editDescription.setText(it.description)
                selectedIcon = it.icon
                selectedColor = it.color
                tvSelectedIcon.text = selectedIcon
                viewSelectedColor.setBackgroundColor(
                    ContextCompat.getColor(this, HabitColors.getColorRes(selectedColor))
                )
                title = "Edit Habit"
                findViewById<Button>(R.id.btnSave).text = "Update"
            }
        }

        tvSelectedIcon.setOnClickListener {
            showIconPicker()
        }

        viewSelectedColor.setOnClickListener {
            showColorPicker()
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = editName.text.toString().trim()
            if (name.isNotEmpty()) {
                if (existingHabit != null) {
                    existingHabit!!.name = name
                    existingHabit!!.description = editDescription.text.toString().trim()
                    existingHabit!!.icon = selectedIcon
                    existingHabit!!.color = selectedColor
                    habitStore.update(existingHabit!!)
                } else {
                    val habit = Habit(
                        name = name,
                        description = editDescription.text.toString().trim(),
                        icon = selectedIcon,
                        color = selectedColor
                    )
                    habitStore.create(habit)
                }
                finish()
            } else {
                editName.error = "Please enter a habit name"
            }
        }

        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }
    }

    private fun showIconPicker() {
        val icons = HabitColors.iconOptions.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select Icon")
            .setItems(icons) { _, which ->
                selectedIcon = icons[which]
                tvSelectedIcon.text = selectedIcon
            }
            .show()
    }

    private fun showColorPicker() {
        val displayNames = HabitColors.colorOptions.map { it.second }.toTypedArray()
        AlertDialog.Builder(this)
            .setTitle("Select Color")
            .setItems(displayNames) { _, which ->
                selectedColor = HabitColors.colorOptions[which].first
                viewSelectedColor.setBackgroundColor(
                    ContextCompat.getColor(this, HabitColors.getColorRes(selectedColor))
                )
            }
            .show()
    }
}
