package org.wit.habit

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.compose.ui.platform.ComposeView
import org.wit.habit.helpers.HabitStore
import org.wit.habit.helpers.ThemeStore
import org.wit.habit.ui.compose.FloatingBottomNav
import org.wit.habit.ui.compose.NavTab
import org.wit.habit.ui.theme.HabitTheme
import timber.log.Timber

class SettingsActivity : BaseActivity() {

    private lateinit var habitStore: HabitStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        habitStore = HabitStore(this)

        val tvAppName: TextView = findViewById(R.id.tvAppName)
        val tvVersion: TextView = findViewById(R.id.tvVersion)
        val btnTheme: Button = findViewById(R.id.btnTheme)
        val btnClearData: Button = findViewById(R.id.btnClearData)
        val btnAbout: Button = findViewById(R.id.btnAbout)
        val btnBack: Button = findViewById(R.id.btnBack)

        tvAppName.text = getString(R.string.app_name)
        tvVersion.text = "Version ${packageManager.getPackageInfo(packageName, 0).versionName}"

        updateThemeButtonText(btnTheme)

        btnTheme.setOnClickListener {
            showThemePicker(btnTheme)
        }

        btnClearData.setOnClickListener {
            AlertDialog.Builder(this)
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
            AlertDialog.Builder(this)
                .setTitle("About")
                .setMessage("${getString(R.string.app_name)}\nVersion ${packageManager.getPackageInfo(packageName, 0).versionName}")
                .setPositiveButton("OK", null)
                .show()
        }

        btnBack.setOnClickListener {
            finish()
        }

        setupBottomNav()
    }

    private fun updateThemeButtonText(button: Button) {
        val currentKey = ThemeStore.getCurrentThemeKey(this)
        val name = ThemeStore.themeOptions.find { it.first == currentKey }?.second ?: "Mint"
        button.text = "Switch Theme (Current: $name)"
    }

    private fun showThemePicker(button: Button) {
        val options = ThemeStore.themeOptions
        val currentKey = ThemeStore.getCurrentThemeKey(this)
        val currentIndex = options.indexOfFirst { it.first == currentKey }.coerceAtLeast(0)
        val displayNames = options.map { it.second }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("Select Theme")
            .setSingleChoiceItems(displayNames, currentIndex) { dialog, which ->
                val selectedKey = options[which].first
                if (selectedKey != currentKey) {
                    ThemeStore.setTheme(this, selectedKey)
                    Timber.i("User switched theme to: $selectedKey")
                    updateThemeButtonText(button)
                    recreate()
                }
                dialog.dismiss()
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun setupBottomNav() {
        val composeNavView = findViewById<ComposeView>(R.id.composeNavView)
        composeNavView.setContent {
            HabitTheme {
                FloatingBottomNav(
                    selectedTab = NavTab.SETTINGS,
                    onTabSelected = { tab ->
                        when (tab) {
                            NavTab.HOME -> {
                                Timber.i("User navigated to Home")
                                finish()
                            }
                            NavTab.STATS -> {
                                Timber.i("User navigated to Stats")
                                startActivity(Intent(this, StatsActivity::class.java))
                                finish()
                            }
                            NavTab.SETTINGS -> {
                                Timber.i("User selected Settings tab (already on Settings)")
                            }
                        }
                    }
                )
            }
        }
    }
}
