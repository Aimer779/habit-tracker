package org.wit.habit

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import org.wit.habit.helpers.HabitStore
import org.wit.habit.model.Habit

class AddHabitActivity : AppCompatActivity() {
    private lateinit var habitStore: HabitStore
    private lateinit var editName: EditText
    private lateinit var editDescription: EditText
    private var existingHabit: Habit? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)

        habitStore = HabitStore(this)
        editName = findViewById(R.id.editName)
        editDescription = findViewById(R.id.editDescription)

        val habitId = intent.getLongExtra("habit_id", -1L)
        if (habitId != -1L) {
            existingHabit = habitStore.findById(habitId)
            existingHabit?.let {
                editName.setText(it.name)
                editDescription.setText(it.description)
                title = "编辑习惯"
                findViewById<Button>(R.id.btnSave).text = "更新"
            }
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = editName.text.toString().trim()
            if (name.isNotEmpty()) {
                if (existingHabit != null) {
                    existingHabit!!.name = name
                    existingHabit!!.description = editDescription.text.toString().trim()
                    habitStore.update(existingHabit!!)
                } else {
                    val habit = Habit(
                        name = name,
                        description = editDescription.text.toString().trim()
                    )
                    habitStore.create(habit)
                }
                finish()
            } else {
                editName.error = "请输入习惯名称"
            }
        }

        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }
    }
}
