package org.wit.habit

import android.content.Intent
import android.os.Bundle
import androidx.activity.OnBackPressedCallback
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.ComposeView
import androidx.core.view.WindowCompat
import androidx.core.view.doOnLayout
import androidx.core.view.updatePadding
import androidx.fragment.app.FragmentContainerView
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import org.wit.habit.ui.add.AddHabitActivity
import org.wit.habit.ui.base.BaseActivity
import org.wit.habit.ui.navigation.FloatingBottomNav
import org.wit.habit.ui.navigation.NavTab
import org.wit.habit.ui.home.HomeFragment
import org.wit.habit.ui.settings.SettingsFragment
import org.wit.habit.ui.stats.StatsFragment
import org.wit.habit.ui.theme.HabitTheme
import timber.log.Timber

class MainActivity : BaseActivity() {

    private var currentTab by mutableStateOf(NavTab.HOME)

    private val backToHomeCallback = object : OnBackPressedCallback(false) {
        override fun handleOnBackPressed() {
            switchTab(NavTab.HOME)
        }
    }

    companion object {
        private const val KEY_CURRENT_TAB = "current_tab"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        WindowCompat.setDecorFitsSystemWindows(window, false)
        setContentView(R.layout.activity_main)

        Timber.i("Habit Tracker started")

        currentTab = savedInstanceState?.getString(KEY_CURRENT_TAB)
            ?.let(NavTab::valueOf) ?: NavTab.HOME
        backToHomeCallback.isEnabled = currentTab != NavTab.HOME

        if (savedInstanceState == null) {
            supportFragmentManager.commit {
                add(R.id.fragmentContainer, HomeFragment(), NavTab.HOME.name)
            }
        }

        onBackPressedDispatcher.addCallback(this, backToHomeCallback)
        setupBottomNav()
        syncFragmentBottomInset()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putString(KEY_CURRENT_TAB, currentTab.name)
    }

    private fun setupBottomNav() {
        findViewById<ComposeView>(R.id.composeNavView).setContent {
            HabitTheme {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .navigationBarsPadding(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // FAB above the nav bar, only on the Home tab
                    if (currentTab == NavTab.HOME) {
                        FloatingActionButton(
                            onClick = {
                                startActivity(
                                    Intent(this@MainActivity, AddHabitActivity::class.java)
                                )
                            },
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary,
                            shape = CircleShape
                        ) {
                            Icon(
                                imageVector = Icons.Default.Add,
                                contentDescription = "Add Habit"
                            )
                        }
                    }

                    FloatingBottomNav(
                        selectedTab = currentTab,
                        onTabSelected = ::switchTab
                    )
                }
            }
        }
    }

    private fun syncFragmentBottomInset() {
        val fragmentContainer = findViewById<FragmentContainerView>(R.id.fragmentContainer)
        val navView = findViewById<ComposeView>(R.id.composeNavView)

        val updatePadding = {
            val navHeight = navView.height
            if (navHeight > 0 && fragmentContainer.paddingBottom != navHeight) {
                fragmentContainer.updatePadding(bottom = navHeight)
            }
        }

        // Apply once after the navigation view has been laid out, and keep
        // listening because its height changes when tabs switch (FAB shown/hidden).
        navView.doOnLayout { updatePadding() }
        navView.addOnLayoutChangeListener { _, _, _, _, _, _, _, _, _ -> updatePadding() }
    }

    private fun switchTab(tab: NavTab) {
        if (tab == currentTab) return
        Timber.i("User switched to tab: $tab")
        currentTab = tab
        backToHomeCallback.isEnabled = tab != NavTab.HOME

        // show/hide keeps each tab's state (view mode, filter, stats period) alive
        supportFragmentManager.commit {
            NavTab.entries.forEach { other ->
                if (other != tab) {
                    supportFragmentManager.findFragmentByTag(other.name)?.let { hide(it) }
                }
            }
            val existing = supportFragmentManager.findFragmentByTag(tab.name)
            if (existing == null) {
                add(R.id.fragmentContainer, createFragment(tab), tab.name)
            } else {
                show(existing)
            }
        }
    }

    private fun createFragment(tab: NavTab): Fragment = when (tab) {
        NavTab.HOME -> HomeFragment()
        NavTab.STATS -> StatsFragment()
        NavTab.SETTINGS -> SettingsFragment()
    }
}
