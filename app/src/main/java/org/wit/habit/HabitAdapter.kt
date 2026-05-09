package org.wit.habit

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.card.MaterialCardView
import org.wit.habit.helpers.DateUtils
import org.wit.habit.helpers.HabitColors
import org.wit.habit.model.Habit

class HabitAdapter(
    private var habits: List<Habit>,
    private val listener: OnHabitClickListener
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    interface OnHabitClickListener {
        fun onCheckInClick(habit: Habit)
        fun onCancelCheckInClick(habit: Habit)
        fun onEditClick(habit: Habit)
        fun onDeleteClick(habit: Habit)
    }

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
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
                val isChecked = habit.checkInDates.contains(dateStr)

                val dotColor = if (isChecked) {
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

            val today = DateUtils.today()
            val isCheckedInToday = habit.checkInDates.contains(today)

            if (isCheckedInToday) {
                btnCheckIn.text = "撤销打卡"
                btnCheckIn.backgroundTintList = android.content.res.ColorStateList.valueOf(android.graphics.Color.parseColor("#F44336"))
                btnCheckIn.setOnClickListener {
                    listener.onCancelCheckInClick(habit)
                }
            } else {
                btnCheckIn.text = "打卡"
                btnCheckIn.backgroundTintList = android.content.res.ColorStateList.valueOf(themeColor)
                btnCheckIn.setOnClickListener {
                    listener.onCheckInClick(habit)
                }
            }

            cardView.setOnClickListener {
                listener.onEditClick(habit)
            }

            cardView.setOnLongClickListener {
                listener.onDeleteClick(habit)
                true
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HabitViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_habit, parent, false)
        return HabitViewHolder(view)
    }

    override fun onBindViewHolder(holder: HabitViewHolder, position: Int) {
        holder.bind(habits[position])
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
