package org.wit.habit

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import org.wit.habit.helpers.DateUtils
import org.wit.habit.model.Habit

class HabitAdapter(
    private var habits: List<Habit>,
    private val listener: OnHabitClickListener
) : RecyclerView.Adapter<HabitAdapter.HabitViewHolder>() {

    interface OnHabitClickListener {
        fun onCheckInClick(habit: Habit)
        fun onCancelCheckInClick(habit: Habit)
        fun onDeleteClick(habit: Habit)
    }

    inner class HabitViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val cardView: MaterialCardView = itemView.findViewById(R.id.cardView)
        private val tvName: TextView = itemView.findViewById(R.id.tvName)
        private val tvDescription: TextView = itemView.findViewById(R.id.tvDescription)
        private val tvStatus: TextView = itemView.findViewById(R.id.tvStatus)
        private val btnCheckIn: Button = itemView.findViewById(R.id.btnCheckIn)
        private val btnDelete: Button = itemView.findViewById(R.id.btnDelete)

        fun bind(habit: Habit) {
            tvName.text = habit.name
            tvDescription.text = habit.description.ifEmpty { "暂无描述" }

            val today = DateUtils.today()
            val isCheckedInToday = habit.checkInDates.contains(today)

            if (isCheckedInToday) {
                tvStatus.text = "今日已打卡"
                tvStatus.setTextColor(itemView.context.getColor(android.R.color.holo_green_dark))
                btnCheckIn.text = "取消打卡"
                btnCheckIn.setOnClickListener {
                    listener.onCancelCheckInClick(habit)
                }
            } else {
                tvStatus.text = "今日未打卡"
                tvStatus.setTextColor(itemView.context.getColor(android.R.color.darker_gray))
                btnCheckIn.text = "打卡"
                btnCheckIn.setOnClickListener {
                    listener.onCheckInClick(habit)
                }
            }

            btnDelete.setOnClickListener {
                listener.onDeleteClick(habit)
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
}
