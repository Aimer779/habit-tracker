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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_habit)

        habitStore = HabitStore(this)
        editName = findViewById(R.id.editName)
        editDescription = findViewById(R.id.editDescription)

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = editName.text.toString().trim()
            if (name.isNotEmpty()) {
                val habit = Habit(
                    name = name,
                    description = editDescription.text.toString().trim()
                )
                habitStore.create(habit)
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
