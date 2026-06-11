package org.wit.habit

import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.GridLayout
import android.widget.TextView
import android.view.View
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.material.card.MaterialCardView
import org.wit.habit.helpers.HabitColors
import org.wit.habit.helpers.HabitStore
import org.wit.habit.model.Habit

class AddHabitActivity : BaseActivity() {
    private lateinit var habitStore: HabitStore
    private lateinit var editName: EditText
    private lateinit var editDescription: EditText
    private lateinit var editTargetCount: EditText
    private lateinit var tvSelectedIcon: TextView
    private lateinit var viewSelectedColor: View
    private var existingHabit: Habit? = null

    private var selectedIcon: String = "✅"
    private var selectedColor: String = "blue"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_add_habit)
        applyInsets(findViewById(android.R.id.content))

        habitStore = HabitStore(this)
        editName = findViewById(R.id.editName)
        editDescription = findViewById(R.id.editDescription)
        editTargetCount = findViewById(R.id.editTargetCount)
        tvSelectedIcon = findViewById(R.id.tvSelectedIcon)
        viewSelectedColor = findViewById(R.id.viewSelectedColor)
        findViewById<Button>(R.id.btnTargetMinus).contentDescription = "Decrease daily target"
        findViewById<Button>(R.id.btnTargetPlus).contentDescription = "Increase daily target"

        val habitId = intent.getLongExtra("habit_id", -1L)
        if (habitId != -1L) {
            existingHabit = habitStore.findById(habitId)
            existingHabit?.let {
                editName.setText(it.name)
                editDescription.setText(it.description)
                selectedIcon = it.icon
                selectedColor = it.color
                editTargetCount.setText(it.targetCount.toString())
                tvSelectedIcon.text = selectedIcon
                viewSelectedColor.setBackgroundColor(
                    ContextCompat.getColor(this, HabitColors.getColorRes(selectedColor))
                )
                title = "Edit Habit"
                findViewById<Button>(R.id.btnSave).text = "Update"
            }
        }
        updateSelectedOptionDescriptions()

        tvSelectedIcon.setOnClickListener {
            showIconPicker()
        }

        viewSelectedColor.setOnClickListener {
            showColorPicker()
        }

        findViewById<Button>(R.id.btnTargetMinus).setOnClickListener {
            updateTargetCount(-1)
        }

        findViewById<Button>(R.id.btnTargetPlus).setOnClickListener {
            updateTargetCount(1)
        }

        findViewById<Button>(R.id.btnSave).setOnClickListener {
            val name = editName.text.toString().trim()
            if (name.isNotEmpty()) {
                val targetCount = editTargetCount.text.toString().toIntOrNull()?.coerceAtLeast(1) ?: 1
                if (existingHabit != null) {
                    existingHabit!!.name = name
                    existingHabit!!.description = editDescription.text.toString().trim()
                    existingHabit!!.icon = selectedIcon
                    existingHabit!!.color = selectedColor
                    existingHabit!!.targetCount = targetCount
                    habitStore.update(existingHabit!!)
                    timber.log.Timber.i("User updated habit: $name")
                } else {
                    val habit = Habit(
                        name = name,
                        description = editDescription.text.toString().trim(),
                        icon = selectedIcon,
                        color = selectedColor,
                        targetCount = targetCount
                    )
                    habitStore.create(habit)
                    timber.log.Timber.i("User created habit: $name")
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

    private fun applyInsets(view: View) {
        val initialLeft = view.paddingLeft
        val initialTop = view.paddingTop
        val initialRight = view.paddingRight
        val initialBottom = view.paddingBottom

        ViewCompat.setOnApplyWindowInsetsListener(view) { target, insets ->
            val bars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            target.setPadding(
                initialLeft + bars.left,
                initialTop + bars.top,
                initialRight + bars.right,
                initialBottom + bars.bottom
            )
            insets
        }
    }

    private fun updateTargetCount(delta: Int) {
        val current = editTargetCount.text.toString().toIntOrNull() ?: 1
        editTargetCount.setText((current + delta).coerceAtLeast(1).toString())
    }

    private fun showIconPicker() {
        val grid = createPickerGrid(columnCount = 4)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Select Icon")
            .setView(grid)
            .create()

        HabitColors.iconOptions.forEach { icon ->
            grid.addView(createIconOption(icon, icon == selectedIcon) {
                selectedIcon = icon
                tvSelectedIcon.text = selectedIcon
                updateSelectedOptionDescriptions()
                dialog.dismiss()
            })
        }

        dialog.show()
    }

    private fun showColorPicker() {
        val grid = createPickerGrid(columnCount = 4)
        val dialog = AlertDialog.Builder(this)
            .setTitle("Select Color")
            .setView(grid)
            .create()

        HabitColors.colorOptions.forEach { (key, name) ->
            grid.addView(createColorOption(key, name, key == selectedColor) {
                selectedColor = key
                viewSelectedColor.setBackgroundColor(
                    ContextCompat.getColor(this, HabitColors.getColorRes(selectedColor))
                )
                updateSelectedOptionDescriptions()
                dialog.dismiss()
            })
        }

        dialog.show()
    }

    private fun createPickerGrid(columnCount: Int): GridLayout {
        return GridLayout(this).apply {
            this.columnCount = columnCount
            setPadding(dp(16), dp(8), dp(16), dp(16))
        }
    }

    private fun createIconOption(icon: String, selected: Boolean, onClick: () -> Unit): View {
        val card = pickerCard(selected)
        card.contentDescription = if (selected) {
            "Icon $icon, selected"
        } else {
            "Icon $icon"
        }
        card.isSelected = selected
        val text = TextView(this).apply {
            text = icon
            textSize = 28f
            gravity = Gravity.CENTER
        }
        card.addView(text, FrameLayout.LayoutParams(dp(56), dp(56)))
        card.setOnClickListener { onClick() }
        return card
    }

    private fun createColorOption(
        colorKey: String,
        label: String,
        selected: Boolean,
        onClick: () -> Unit
    ): View {
        val card = pickerCard(selected)
        card.contentDescription = if (selected) {
            "$label color, selected"
        } else {
            "$label color"
        }
        card.isSelected = selected
        val container = android.widget.LinearLayout(this).apply {
            orientation = android.widget.LinearLayout.VERTICAL
            gravity = Gravity.CENTER
            setPadding(dp(6), dp(6), dp(6), dp(4))
        }
        val swatch = View(this).apply {
            background = android.graphics.drawable.GradientDrawable().apply {
                shape = android.graphics.drawable.GradientDrawable.OVAL
                setColor(ContextCompat.getColor(this@AddHabitActivity, HabitColors.getColorRes(colorKey)))
            }
        }
        val text = TextView(this).apply {
            text = label
            textSize = 11f
            gravity = Gravity.CENTER
            maxLines = 1
        }
        container.addView(swatch, android.widget.LinearLayout.LayoutParams(dp(32), dp(32)))
        container.addView(text, android.widget.LinearLayout.LayoutParams(dp(64), dp(20)))
        card.addView(container, FrameLayout.LayoutParams(dp(72), dp(72)))
        card.setOnClickListener { onClick() }
        return card
    }

    private fun updateSelectedOptionDescriptions() {
        tvSelectedIcon.contentDescription = "Selected icon $selectedIcon. Double tap to change icon."
        viewSelectedColor.contentDescription = "Selected color ${selectedColorLabel()}. Double tap to change color."
    }

    private fun selectedColorLabel(): String {
        return HabitColors.colorOptions.firstOrNull { (key, _) -> key == selectedColor }?.second
            ?: selectedColor
    }

    private fun pickerCard(selected: Boolean): MaterialCardView {
        val margin = dp(6)
        return MaterialCardView(this).apply {
            radius = dp(12).toFloat()
            cardElevation = 0f
            setCardBackgroundColor(resolveThemeColor(com.google.android.material.R.attr.colorSurfaceContainerLow))
            strokeWidth = if (selected) dp(2) else dp(1)
            strokeColor = resolveThemeColor(
                if (selected) {
                    androidx.appcompat.R.attr.colorPrimary
                } else {
                    com.google.android.material.R.attr.colorOutlineVariant
                }
            )
            isClickable = true
            isFocusable = true
            foreground = resolveDrawable(android.R.attr.selectableItemBackground)
            layoutParams = GridLayout.LayoutParams().apply {
                width = GridLayout.LayoutParams.WRAP_CONTENT
                height = GridLayout.LayoutParams.WRAP_CONTENT
                setMargins(margin, margin, margin, margin)
            }
        }
    }

    private fun resolveThemeColor(attr: Int): Int {
        val typedValue = android.util.TypedValue()
        theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    private fun resolveDrawable(attr: Int): android.graphics.drawable.Drawable? {
        val typedValue = android.util.TypedValue()
        theme.resolveAttribute(attr, typedValue, true)
        return ContextCompat.getDrawable(this, typedValue.resourceId)
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
