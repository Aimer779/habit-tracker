package org.wit.habit.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import org.wit.habit.data.local.ThemeStore

abstract class BaseActivity : AppCompatActivity() {
    private var appliedTheme: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        appliedTheme = ThemeStore.getThemeRes(this)
        setTheme(appliedTheme)
        super.onCreate(savedInstanceState)
    }

    override fun onResume() {
        super.onResume()
        if (ThemeStore.getThemeRes(this) != appliedTheme) {
            recreate()
        }
    }
}
