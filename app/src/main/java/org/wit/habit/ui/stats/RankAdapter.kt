package org.wit.habit.ui.stats

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.card.MaterialCardView
import com.google.android.material.color.MaterialColors
import com.google.android.material.progressindicator.LinearProgressIndicator
import org.wit.habit.R

data class RankItem(val icon: String, val name: String, val count: Int)

class RankAdapter(
    private var items: List<RankItem> = emptyList(),
    private var maxCount: Int = 0
) : RecyclerView.Adapter<RankAdapter.ViewHolder>() {

    fun submitItems(items: List<RankItem>, maxCount: Int) {
        this.items = items
        this.maxCount = maxCount
        notifyDataSetChanged()
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val cardRankBadge: MaterialCardView = itemView.findViewById(R.id.cardRankBadge)
        val tvRank: TextView = itemView.findViewById(R.id.tvRank)
        val tvHabitIcon: TextView = itemView.findViewById(R.id.tvHabitIcon)
        val tvHabitName: TextView = itemView.findViewById(R.id.tvHabitName)
        val tvCheckInCount: TextView = itemView.findViewById(R.id.tvCheckInCount)
        val progressBar: LinearProgressIndicator = itemView.findViewById(R.id.progressBar)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_stats_rank, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = items[position]
        val rank = position + 1
        val context = holder.itemView.context

        holder.tvHabitIcon.text = item.icon
        holder.tvHabitName.text = item.name
        holder.tvCheckInCount.text = context.getString(R.string.rank_days, item.count)

        val progress = if (maxCount > 0) item.count.toFloat() / maxCount else 0f
        holder.progressBar.setProgress((progress * 100).toInt(), false)

        when (rank) {
            1 -> {
                holder.tvRank.text = "🥇"
                holder.cardRankBadge.setCardBackgroundColor(
                    MaterialColors.getColor(context, com.google.android.material.R.attr.colorPrimaryContainer, 0)
                )
            }
            2 -> {
                holder.tvRank.text = "🥈"
                holder.cardRankBadge.setCardBackgroundColor(
                    MaterialColors.getColor(context, com.google.android.material.R.attr.colorSecondaryContainer, 0)
                )
            }
            3 -> {
                holder.tvRank.text = "🥉"
                holder.cardRankBadge.setCardBackgroundColor(
                    MaterialColors.getColor(context, com.google.android.material.R.attr.colorTertiaryContainer, 0)
                )
            }
            else -> {
                holder.tvRank.text = rank.toString()
                holder.cardRankBadge.setCardBackgroundColor(
                    MaterialColors.getColor(context, com.google.android.material.R.attr.colorSurfaceVariant, 0)
                )
            }
        }
    }

    override fun getItemCount() = items.size
}
