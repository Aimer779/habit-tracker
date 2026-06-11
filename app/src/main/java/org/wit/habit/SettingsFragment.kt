package org.wit.habit

import android.app.AlertDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
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
        val btnTheme: Button = view.findViewById(R.id.btnTheme)
        val btnClearData: Button = view.findViewById(R.id.btnClearData)
        val btnAbout: Button = view.findViewById(R.id.btnAbout)

        val packageManager = requireContext().packageManager
        val packageName = requireContext().packageName
        val versionName = packageManager.getPackageInfo(packageName, 0).versionName

        tvAppName.text = getString(R.string.app_name)
        tvVersion.text = "Version $versionName"

        updateThemeButtonText(btnTheme)

        btnTheme.setOnClickListener {
            showThemePicker(btnTheme)
        }

        btnClearData.setOnClickListener {
            AlertDialog.Builder(requireContext())
                .setTitle("Confirm Clear")
                .setMessage("Are you sure you want to clear all habit data? This action cannot be undone.")
                .setPositiveButton("OK") { _, _ ->
                    habitStore.clearAll()
                    Timber.i("User cleared all habit data")
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        btnAbout.setOnClickListener {
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

    private fun updateThemeButtonText(button: Button) {
        val currentKey = ThemeStore.getCurrentThemeKey(requireContext())
        val name = ThemeStore.themeOptions.find { it.first == currentKey }?.second ?: "Mint"
        button.text = "Switch Theme (Current: $name)"
    }

    private fun showThemePicker(button: Button) {
        val options = ThemeStore.themeOptions
        val currentKey = ThemeStore.getCurrentThemeKey(requireContext())
        val currentIndex = options.indexOfFirst { it.first == currentKey }.coerceAtLeast(0)
        val displayNames = options.map { it.second }.toTypedArray()

        AlertDialog.Builder(requireContext())
            .setTitle("Select Theme")
            .setSingleChoiceItems(displayNames, currentIndex) { dialog, which ->
                val selectedKey = options[which].first
                if (selectedKey != currentKey) {
                    ThemeStore.setTheme(requireContext(), selectedKey)
                    Timber.i("User switched theme to: $selectedKey")
                    updateThemeButtonText(button)
                    requireActivity().recreate()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
