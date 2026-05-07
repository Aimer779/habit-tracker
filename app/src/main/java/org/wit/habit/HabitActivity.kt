package org.wit.habit

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import org.wit.habit.helpers.HabitStore
import org.wit.habit.model.Habit

class HabitActivity : AppCompatActivity() {
    private lateinit var habitStore: HabitStore
    private lateinit var titleEdit: EditText
    private lateinit var descEdit: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_habit)

        habitStore = HabitStore(this)
        titleEdit = findViewById(R.id.editTitle)
        descEdit = findViewById(R.id.editDescription)

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val title = titleEdit.text.toString().trim()
            if (title.isNotEmpty()) {
                val habit = Habit(
                    title = title,
                    description = descEdit.text.toString().trim()
                )
                habitStore.create(habit)
                finish()
            }
        }

        findViewById<Button>(R.id.btnCancel).setOnClickListener {
            finish()
        }
    }
}
