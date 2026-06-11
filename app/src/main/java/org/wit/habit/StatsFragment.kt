package org.wit.habit

import android.os.Bundle
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.wit.habit.helpers.DateUtils
import org.wit.habit.helpers.HabitStore
import org.wit.habit.model.Habit
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class StatsFragment : Fragment() {

    private lateinit var habitStore: HabitStore
    private lateinit var tvPeriod: TextView
    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var tabWeek: TextView
    private lateinit var tabMonth: TextView
    private lateinit var tabYear: TextView
    private lateinit var tvTotalCheckIns: TextView
    private lateinit var tvCurrentStreak: TextView
    private lateinit var tvLongestStreak: TextView
    private lateinit var tvActiveDays: TextView
    private lateinit var rvRankings: RecyclerView

    private var currentYear = 0
    private var currentMonth = 0
    private var currentWeekStart = ""
    private var currentTab = TAB_MONTH

    companion object {
        const val TAB_WEEK = 0
        const val TAB_MONTH = 1
        const val TAB_YEAR = 2
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_stats, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        habitStore = HabitStore(requireContext())
        applyInsets(view)

        val cal = Calendar.getInstance()
        currentYear = cal.get(Calendar.YEAR)
        currentMonth = cal.get(Calendar.MONTH) + 1

        initViews(view)
        setupListeners()
        updateUI()
    }

    override fun onHiddenChanged(hidden: Boolean) {
        super.onHiddenChanged(hidden)
        if (!hidden) {
            // Refresh stats when this tab becomes visible again
            updateUI()
        }
    }

    private fun initViews(view: View) {
        tvPeriod = view.findViewById(R.id.tvPeriod)
        btnPrev = view.findViewById(R.id.btnPrev)
        btnNext = view.findViewById(R.id.btnNext)
        tabWeek = view.findViewById(R.id.tabWeek)
        tabMonth = view.findViewById(R.id.tabMonth)
        tabYear = view.findViewById(R.id.tabYear)
        tvTotalCheckIns = view.findViewById(R.id.tvTotalCheckIns)
        tvCurrentStreak = view.findViewById(R.id.tvCurrentStreak)
        tvLongestStreak = view.findViewById(R.id.tvLongestStreak)
        tvActiveDays = view.findViewById(R.id.tvActiveDays)
        rvRankings = view.findViewById(R.id.rvRankings)

        rvRankings.layoutManager = LinearLayoutManager(requireContext())
    }

    private fun setupListeners() {
        tabWeek.setOnClickListener { switchTab(TAB_WEEK) }
        tabMonth.setOnClickListener { switchTab(TAB_MONTH) }
        tabYear.setOnClickListener { switchTab(TAB_YEAR) }

        btnPrev.setOnClickListener { navigatePeriod(-1) }
        btnNext.setOnClickListener { navigatePeriod(1) }
    }

    private fun switchTab(tab: Int) {
        currentTab = tab
        updateTabStyles()

        val cal = Calendar.getInstance()
        currentYear = cal.get(Calendar.YEAR)
        currentMonth = cal.get(Calendar.MONTH) + 1
        currentWeekStart = DateUtils.getWeekStart(DateUtils.today())

        updateUI()
    }

    private fun updateTabStyles() {
        val tabs = listOf(tabWeek, tabMonth, tabYear)
        val selectedBg = R.drawable.bg_tab_selected
        val normalBg = R.drawable.bg_tab_normal

        tabs.forEachIndexed { index, textView ->
            if (index == currentTab) {
                textView.setBackgroundResource(selectedBg)
                textView.setTextColor(resolveThemeColor(com.google.android.material.R.attr.colorOnPrimary))
            } else {
                textView.setBackgroundResource(normalBg)
                textView.setTextColor(resolveThemeColor(com.google.android.material.R.attr.colorOnSurface))
            }
        }
    }

    private fun resolveThemeColor(attr: Int): Int {
        val typedValue = TypedValue()
        requireContext().theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
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
                initialBottom
            )
            insets
        }
    }

    private fun navigatePeriod(direction: Int) {
        when (currentTab) {
            TAB_YEAR -> {
                currentYear += direction
            }
            TAB_WEEK -> {
                currentWeekStart = DateUtils.addDays(currentWeekStart, direction * 7)
            }
            else -> {
                currentMonth += direction
                if (currentMonth > 12) {
                    currentMonth = 1
                    currentYear++
                } else if (currentMonth < 1) {
                    currentMonth = 12
                    currentYear--
                }
            }
        }
        updateUI()
    }

    private fun updateUI() {
        updatePeriodLabel()
        updateNextButtonState()
        loadStats()
    }

    private fun updatePeriodLabel() {
        tvPeriod.text = when (currentTab) {
            TAB_WEEK -> {
                val weekEnd = DateUtils.addDays(currentWeekStart, 6)
                val startParts = currentWeekStart.split("-")
                val endParts = weekEnd.split("-")
                "${startParts[1]}.${startParts[2]} - ${endParts[1]}.${endParts[2]}"
            }
            TAB_MONTH -> "$currentYear.$currentMonth"
            TAB_YEAR -> "$currentYear"
            else -> "$currentYear.$currentMonth"
        }
    }

    private fun updateNextButtonState() {
        val cal = Calendar.getInstance()
        val nowYear = cal.get(Calendar.YEAR)
        val nowMonth = cal.get(Calendar.MONTH) + 1
        val today = DateUtils.today()

        val isFuture = when (currentTab) {
            TAB_WEEK -> {
                val weekEnd = DateUtils.addDays(currentWeekStart, 6)
                weekEnd > today
            }
            TAB_MONTH -> DateUtils.isAfterMonth(currentYear, currentMonth, nowYear, nowMonth)
            TAB_YEAR -> currentYear > nowYear
            else -> false
        }

        btnNext.isEnabled = !isFuture
        btnNext.alpha = if (isFuture) 0.3f else 1.0f
    }

    private fun loadStats() {
        val habits = habitStore.findAll()
        val today = DateUtils.today()

        when (currentTab) {
            TAB_MONTH -> loadMonthStats(habits, today)
            TAB_YEAR -> loadYearStats(habits, today)
            TAB_WEEK -> loadWeekStats(habits, today)
            else -> loadMonthStats(habits, today)
        }
    }

    private fun loadMonthStats(habits: List<Habit>, today: String) {
        val firstDay = DateUtils.getFirstDayOfMonth(currentYear, currentMonth)
        val lastDay = DateUtils.getLastDayOfMonth(currentYear, currentMonth)

        var totalCheckIns = 0
        val activeDaysSet = mutableSetOf<String>()
        val habitMonthCounts = mutableMapOf<Long, Int>()

        habits.forEach { habit ->
            var monthCount = 0
            habit.checkInCounts.forEach { (date, count) ->
                if (date in firstDay..lastDay) {
                    totalCheckIns += count
                    activeDaysSet.add(date)
                    monthCount += count
                }
            }
            habitMonthCounts[habit.id] = monthCount
        }

        tvTotalCheckIns.text = totalCheckIns.toString()
        tvActiveDays.text = activeDaysSet.size.toString()
        tvCurrentStreak.text = calculateCurrentStreak(habits, today).toString()
        tvLongestStreak.text = calculateLongestStreak(habits).toString()

        val rankings = habits
            .filter { (habitMonthCounts[it.id] ?: 0) > 0 }
            .sortedByDescending { habitMonthCounts[it.id] ?: 0 }
            .map {
                RankItem(
                    icon = it.icon,
                    name = it.name,
                    count = habitMonthCounts[it.id] ?: 0
                )
            }

        rvRankings.adapter = RankAdapter(rankings)
    }

    private fun loadWeekStats(habits: List<Habit>, today: String) {
        val weekEnd = DateUtils.addDays(currentWeekStart, 6)

        var totalCheckIns = 0
        val activeDaysSet = mutableSetOf<String>()
        val habitWeekCounts = mutableMapOf<Long, Int>()

        habits.forEach { habit ->
            var weekCount = 0
            habit.checkInCounts.forEach { (date, count) ->
                if (date in currentWeekStart..weekEnd) {
                    totalCheckIns += count
                    activeDaysSet.add(date)
                    weekCount += count
                }
            }
            habitWeekCounts[habit.id] = weekCount
        }

        tvTotalCheckIns.text = totalCheckIns.toString()
        tvActiveDays.text = activeDaysSet.size.toString()
        tvCurrentStreak.text = calculateCurrentStreak(habits, today).toString()
        tvLongestStreak.text = calculateLongestStreak(habits).toString()

        val rankings = habits
            .filter { (habitWeekCounts[it.id] ?: 0) > 0 }
            .sortedByDescending { habitWeekCounts[it.id] ?: 0 }
            .map {
                RankItem(
                    icon = it.icon,
                    name = it.name,
                    count = habitWeekCounts[it.id] ?: 0
                )
            }

        rvRankings.adapter = RankAdapter(rankings)
    }

    private fun loadYearStats(habits: List<Habit>, today: String) {
        val yearPrefix = "$currentYear-"

        var totalCheckIns = 0
        val activeDaysSet = mutableSetOf<String>()
        val habitYearCounts = mutableMapOf<Long, Int>()

        habits.forEach { habit ->
            var yearCount = 0
            habit.checkInCounts.forEach { (date, count) ->
                if (date.startsWith(yearPrefix)) {
                    totalCheckIns += count
                    activeDaysSet.add(date)
                    yearCount += count
                }
            }
            habitYearCounts[habit.id] = yearCount
        }

        tvTotalCheckIns.text = totalCheckIns.toString()
        tvActiveDays.text = activeDaysSet.size.toString()
        tvCurrentStreak.text = calculateCurrentStreak(habits, today).toString()
        tvLongestStreak.text = calculateLongestStreak(habits).toString()

        val rankings = habits
            .filter { (habitYearCounts[it.id] ?: 0) > 0 }
            .sortedByDescending { habitYearCounts[it.id] ?: 0 }
            .map {
                RankItem(
                    icon = it.icon,
                    name = it.name,
                    count = habitYearCounts[it.id] ?: 0
                )
            }

        rvRankings.adapter = RankAdapter(rankings)
    }

    private fun calculateCurrentStreak(habits: List<Habit>, today: String): Int {
        val allDates = mutableSetOf<String>()
        habits.forEach { habit ->
            allDates.addAll(habit.checkInCounts.keys)
        }

        if (allDates.isEmpty()) return 0

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val cal = Calendar.getInstance()
        val todayDate = formatter.parse(today) ?: return 0

        var streak = 0
        cal.time = todayDate

        while (true) {
            val dateStr = formatter.format(cal.time)
            if (allDates.contains(dateStr)) {
                streak++
            } else {
                if (streak == 0 && dateStr == today) {
                    return 0
                }
                break
            }
            cal.add(Calendar.DAY_OF_YEAR, -1)
        }

        return streak
    }

    private fun calculateLongestStreak(habits: List<Habit>): Int {
        val allDates = mutableSetOf<String>()
        habits.forEach { habit ->
            allDates.addAll(habit.checkInCounts.keys)
        }

        if (allDates.isEmpty()) return 0

        val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val sortedDates = allDates.mapNotNull { formatter.parse(it) }.sorted()

        if (sortedDates.isEmpty()) return 0

        var maxStreak = 1
        var currentStreak = 1
        val cal = Calendar.getInstance()

        for (i in 1 until sortedDates.size) {
            val prev = sortedDates[i - 1]
            val curr = sortedDates[i]

            cal.time = prev
            cal.add(Calendar.DAY_OF_YEAR, 1)

            if (formatter.format(cal.time) == formatter.format(curr)) {
                currentStreak++
                maxStreak = maxOf(maxStreak, currentStreak)
            } else {
                currentStreak = 1
            }
        }

        return maxStreak
    }

    data class RankItem(val icon: String, val name: String, val count: Int)

    inner class RankAdapter(private val items: List<RankItem>) :
        RecyclerView.Adapter<RankAdapter.ViewHolder>() {

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            val tvRank: TextView = itemView.findViewById(R.id.tvRank)
            val tvHabitIcon: TextView = itemView.findViewById(R.id.tvHabitIcon)
            val tvHabitName: TextView = itemView.findViewById(R.id.tvHabitName)
            val tvCheckInCount: TextView = itemView.findViewById(R.id.tvCheckInCount)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val view = LayoutInflater.from(parent.context)
                .inflate(R.layout.item_stats_rank, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val item = items[position]
            holder.tvRank.text = (position + 1).toString()
            holder.tvHabitIcon.text = item.icon
            holder.tvHabitName.text = item.name
            holder.tvCheckInCount.text = "${item.count} check-ins"
        }

        override fun getItemCount() = items.size
    }
}
