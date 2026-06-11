package org.wit.habit

import android.app.AlertDialog
import android.os.Bundle
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import org.wit.habit.helpers.HabitStore
import org.wit.habit.helpers.ThemeStore
import timber.log.Timber

class SettingsFragment : Fragment() {

    private lateinit var habitStore: HabitStore

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return inflater.inflate(R.layout.fragment_settings, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        habitStore = HabitStore(requireContext())
        applyInsets(view)

        val tvAppName: TextView = view.findViewById(R.id.tvAppName)
        val tvVersion: TextView = view.findViewById(R.id.tvVersion)
        val tvThemeSummary: TextView = view.findViewById(R.id.tvThemeSummary)
        val viewThemeSwatch: View = view.findViewById(R.id.viewThemeSwatch)
        val rowTheme: View = view.findViewById(R.id.rowTheme)
        val rowClearData: View = view.findViewById(R.id.rowClearData)
        val rowAbout: View = view.findViewById(R.id.rowAbout)

        val packageManager = requireContext().packageManager
        val packageName = requireContext().packageName
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName

        tvAppName.text = getString(R.string.app_name)
        tvVersion.text = "Version $versionName"

        updateThemeSummary(tvThemeSummary, viewThemeSwatch)

        rowTheme.setOnClickListener {
            showThemePicker(tvThemeSummary, viewThemeSwatch)
        }

        rowClearData.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Clear")
                .setMessage("Are you sure you want to clear all habit data? This action cannot be undone.")
                .setPositiveButton("Clear Data") { _, _ ->
                    habitStore.clearAll()
                    Timber.i("User cleared all habit data")
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        rowAbout.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("About")
                .setMessage("${getString(R.string.app_name)}\nVersion $versionName")
                .setPositiveButton("OK", null)
                .show()
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

    private fun updateThemeSummary(summary: TextView, swatch: View) {
        val currentKey = ThemeStore.getCurrentThemeKey(requireContext())
        val name = ThemeStore.themeOptions.find { it.first == currentKey }?.second ?: "Mint"
        summary.text = name
        swatch.backgroundTintList = android.content.res.ColorStateList.valueOf(
            ContextCompat.getColor(requireContext(), themeColorRes(currentKey))
        )
    }

    private fun showThemePicker(summary: TextView, swatch: View) {
        val options = ThemeStore.themeOptions
        val currentKey = ThemeStore.getCurrentThemeKey(requireContext())
        val currentIndex = options.indexOfFirst { it.first == currentKey }.coerceAtLeast(0)
        val adapter = object : ArrayAdapter<Pair<String, String>>(
            requireContext(),
            android.R.layout.simple_list_item_single_choice,
            options
        ) {
            override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
                val row = LinearLayout(context).apply {
                    orientation = LinearLayout.HORIZONTAL
                    gravity = Gravity.CENTER_VERTICAL
                    setPadding(dp(20), dp(10), dp(20), dp(10))
                }
                val (key, name) = getItem(position) ?: options[position]
                val swatchView = View(context).apply {
                    background = android.graphics.drawable.GradientDrawable().apply {
                        shape = android.graphics.drawable.GradientDrawable.OVAL
                        setColor(ContextCompat.getColor(context, themeColorRes(key)))
                    }
                }
                val label = TextView(context).apply {
                    text = name
                    textSize = 16f
                    setTextColor(resolveThemeColor(com.google.android.material.R.attr.colorOnSurface))
                }
                row.addView(swatchView, LinearLayout.LayoutParams(dp(24), dp(24)).apply {
                    marginEnd = dp(16)
                })
                row.addView(label, LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f))
                if (key == currentKey) {
                    row.addView(ImageView(context).apply {
                        setImageResource(android.R.drawable.checkbox_on_background)
                        imageTintList = android.content.res.ColorStateList.valueOf(
                            resolveThemeColor(androidx.appcompat.R.attr.colorPrimary)
                        )
                    }, LinearLayout.LayoutParams(dp(24), dp(24)))
                }
                row.contentDescription = if (key == currentKey) "$name, selected" else name
                return row
            }
        }

        AlertDialog.Builder(requireContext())
            .setTitle("Select Theme")
            .setSingleChoiceItems(adapter, currentIndex) { dialog, which ->
                val selectedKey = options[which].first
                if (selectedKey != currentKey) {
                    ThemeStore.setTheme(requireContext(), selectedKey)
                    Timber.i("User switched theme to: $selectedKey")
                    updateThemeSummary(summary, swatch)
                    requireActivity().recreate()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun themeColorRes(themeKey: String): Int = when (themeKey) {
        "blue" -> R.color.blue_primary
        "red" -> R.color.red_primary
        "green" -> R.color.green_primary
        "purple" -> R.color.purple_primary
        "yellow" -> R.color.yellow_primary
        else -> R.color.mint_primary
    }

    private fun resolveThemeColor(attr: Int): Int {
        val typedValue = android.util.TypedValue()
        requireContext().theme.resolveAttribute(attr, typedValue, true)
        return typedValue.data
    }

    private fun dp(value: Int): Int {
        return (value * resources.displayMetrics.density).toInt()
    }
}
