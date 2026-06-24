package org.wit.habit.ui.stats

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.button.MaterialButtonToggleGroup
import org.wit.habit.R
import org.wit.habit.utils.DateUtils
import org.wit.habit.data.local.HabitStore
import java.util.Calendar

class StatsFragment : Fragment() {

    private lateinit var habitStore: HabitStore
    private lateinit var tvPeriod: TextView
    private lateinit var btnPrev: TextView
    private lateinit var btnNext: TextView
    private lateinit var periodToggleGroup: MaterialButtonToggleGroup
    private lateinit var tvTotalCheckIns: TextView
    private lateinit var tvCurrentStreak: TextView
    private lateinit var tvLongestStreak: TextView
    private lateinit var tvActiveDays: TextView
    private lateinit var rvRankings: RecyclerView
    private lateinit var rankAdapter: RankAdapter

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
        periodToggleGroup = view.findViewById(R.id.periodToggleGroup)
        tvTotalCheckIns = view.findViewById(R.id.tvTotalCheckIns)
        tvCurrentStreak = view.findViewById(R.id.tvCurrentStreak)
        tvLongestStreak = view.findViewById(R.id.tvLongestStreak)
        tvActiveDays = view.findViewById(R.id.tvActiveDays)
        rvRankings = view.findViewById(R.id.rvRankings)

        rvRankings.layoutManager = LinearLayoutManager(requireContext())
        rankAdapter = RankAdapter()
        rvRankings.adapter = rankAdapter
    }

    private fun setupListeners() {
        periodToggleGroup.addOnButtonCheckedListener { _, checkedId, isChecked ->
            if (isChecked) {
                switchTab(
                    when (checkedId) {
                        R.id.tabWeek -> TAB_WEEK
                        R.id.tabYear -> TAB_YEAR
                        else -> TAB_MONTH
                    }
                )
            }
        }

        btnPrev.setOnClickListener { navigatePeriod(-1) }
        btnNext.setOnClickListener { navigatePeriod(1) }
    }

    private fun switchTab(tab: Int) {
        if (currentTab == tab) return
        currentTab = tab
        updateTabSelection()

        val cal = Calendar.getInstance()
        currentYear = cal.get(Calendar.YEAR)
        currentMonth = cal.get(Calendar.MONTH) + 1
        currentWeekStart = DateUtils.getWeekStart(DateUtils.today())

        updateUI()
    }

    private fun updateTabSelection() {
        val checkedId = when (currentTab) {
            TAB_WEEK -> R.id.tabWeek
            TAB_YEAR -> R.id.tabYear
            else -> R.id.tabMonth
        }
        if (periodToggleGroup.checkedButtonId != checkedId) {
            periodToggleGroup.check(checkedId)
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
        val period = when (currentTab) {
            TAB_WEEK -> StatsPeriod.Week(currentWeekStart)
            TAB_YEAR -> StatsPeriod.Year(currentYear)
            else -> StatsPeriod.Month(currentYear, currentMonth)
        }
        val summary = StatsCalculator.calculate(habits, period, today)

        tvTotalCheckIns.text = summary.totalCheckIns.toString()
        tvActiveDays.text = summary.activeDays.toString()
        tvCurrentStreak.text = summary.currentStreak.toString()
        tvLongestStreak.text = summary.longestStreak.toString()

        rankAdapter.submitItems(summary.rankings, summary.maxCount)
    }

}
