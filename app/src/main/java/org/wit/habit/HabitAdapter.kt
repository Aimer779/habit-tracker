package org.wit.habit

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import org.wit.habit.helpers.DateUtils
import org.wit.habit.helpers.HabitColors
import org.wit.habit.model.Habit
import timber.log.Timber

class HabitAdapter(
    private var habits: List<Habit>,
    private val viewMode: ViewMode,
    private val listener: OnHabitClickListener
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    enum class ViewMode { MONTH, WEEK, DAY }

    interface OnHabitClickListener {
        fun onCheckInClick(habit: Habit)
        fun onCancelCheckInClick(habit: Habit)
        fun onEditClick(habit: Habit)
        fun onDeleteClick(habit: Habit)
    }

    inner class MonthViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
        private val tvIcon: TextView = itemView.findViewById(R.id.tvIcon)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val heatmapGrid: GridLayout = itemView.findViewById(R.id.heatmapGrid)
        private val btnCheckIn: MaterialButton = itemView.findViewById(R.id.btnCheckIn)

        fun bind(habit: Habit) {
            tvIcon.text = habit.icon
            tvName.text = habit.name

            val themeColor = ContextCompat.getColor(itemView.context, HabitColors.getColorRes(habit.color))
            cardView.setCardBackgroundColor(android.graphics.Color.WHITE)

            heatmapGrid.removeAllViews()
            for (i in 34 downTo 0) {
                val dateStr = DateUtils.daysAgo(i)
                val count = habit.checkInCounts[dateStr] ?: 0
                val isCompleted = count >= habit.targetCount

                val dotColor = if (isCompleted) {
                    themeColor
                } else {
                    android.graphics.Color.parseColor("#BDBDBD")
                }

                val dot = View(itemView.context)
                val drawable = GradientDrawable()
                drawable.shape = GradientDrawable.OVAL
                drawable.setColor(dotColor)
                dot.background = drawable

                val size = dpToPx(8, itemView.context)
                val margin = dpToPx(2, itemView.context)
                val params = GridLayout.LayoutParams()
                params.width = size
                params.height = size
                params.setMargins(margin, margin, margin, margin)
                dot.layoutParams = params

                heatmapGrid.addView(dot)
            }

            setupCheckInButton(btnCheckIn, habit, themeColor)

            cardView.setOnClickListener {
                listener.onEditClick(habit)
            }
            cardView.setOnLongClickListener {
                listener.onDeleteClick(habit)
                true
            }
        }
    }

    inner class WeekViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvIcon: TextView = itemView.findViewById(R.id.tvIcon)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val btnCheckIn: MaterialButton = itemView.findViewById(R.id.btnCheckIn)
        private val dotIds = listOf(R.id.dot0, R.id.dot1, R.id.dot2, R.id.dot3, R.id.dot4, R.id.dot5, R.id.dot6)

        fun bind(habit: Habit) {
            tvIcon.text = habit.icon
            tvName.text = habit.name

            val themeColor = ContextCompat.getColor(itemView.context, HabitColors.getColorRes(habit.color))

            for (i in 0..6) {
                val dateStr = DateUtils.daysAgo(6 - i)
                val count = habit.checkInCounts[dateStr] ?: 0
                val isCompleted = count >= habit.targetCount

                val dot = itemView.findViewById<View>(dotIds[i])
                val drawable = GradientDrawable()
                drawable.shape = GradientDrawable.OVAL
                drawable.setColor(if (isCompleted) themeColor else android.graphics.Color.parseColor("#BDBDBD"))
                dot.background = drawable
            }

            setupCheckInButton(btnCheckIn, habit, themeColor)
        }
    }

    inner class DayViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
        private val tvIcon: TextView = itemView.findViewById(R.id.tvIcon)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvProgress: TextView = itemView.findViewById(R.id.tvProgress)
        private val btnCheckIn: MaterialButton = itemView.findViewById(R.id.btnCheckIn)

        fun bind(habit: Habit) {
            tvIcon.text = habit.icon
            tvName.text = habit.name

            val today = DateUtils.today()
            val count = habit.checkInCounts[today] ?: 0
            tvProgress.text = "今日进度：$count/${habit.targetCount}"

            val themeColor = ContextCompat.getColor(itemView.context, HabitColors.getColorRes(habit.color))
            cardView.setCardBackgroundColor(android.graphics.Color.WHITE)

            setupCheckInButton(btnCheckIn, habit, themeColor)

            cardView.setOnClickListener {
                listener.onEditClick(habit)
            }
            cardView.setOnLongClickListener {
                listener.onDeleteClick(habit)
                true
            }
        }
    }

    private fun setupCheckInButton(button: MaterialButton, habit: Habit, themeColor: Int) {
        val today = DateUtils.today()
        val count = habit.checkInCounts[today] ?: 0
        val isCompleted = count >= habit.targetCount

        if (isCompleted) {
            button.text = "撤销打卡"
            button.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F44336"))
            button.setOnClickListener {
                listener.onCancelCheckInClick(habit)
            }
        } else {
            button.text = "打卡"
            button.backgroundTintList = android.content.res.ColorStateList.valueOf(themeColor)
            button.setOnClickListener {
                listener.onCheckInClick(habit)
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return when (viewMode) {
            ViewMode.MONTH -> 0
            ViewMode.WEEK -> 1
            ViewMode.DAY -> 2
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        return when (viewType) {
            0 -> MonthViewHolder(inflater.inflate(R.layout.item_habit, parent, false))
            1 -> WeekViewHolder(inflater.inflate(R.layout.item_habit_week, parent, false))
            2 -> DayViewHolder(inflater.inflate(R.layout.item_habit_day, parent, false))
            else -> MonthViewHolder(inflater.inflate(R.layout.item_habit, parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val habit = habits[position]
        when (holder) {
            is MonthViewHolder -> holder.bind(habit)
            is WeekViewHolder -> holder.bind(habit)
            is DayViewHolder -> holder.bind(habit)
        }
    }

    override fun getItemCount(): Int = habits.size

    fun updateData(newHabits: List<Habit>) {
        habits = newHabits
        notifyDataSetChanged()
    }

    private fun dpToPx(dp: Int, context: Context): Int {
        return (dp * context.resources.displayMetrics.density).toInt()
    }
}
