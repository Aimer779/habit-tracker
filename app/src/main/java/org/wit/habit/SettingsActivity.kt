package org.wit.habit

import android.app.AlertDialog
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import org.wit.habit.helpers.HabitStore
import org.wit.habit.helpers.ThemeStore

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
        tvVersion.text = "版本：${packageManager.getPackageInfo(packageName, 0).versionName}"

        updateThemeButtonText(btnTheme)

        btnTheme.setOnClickListener {
            showThemePicker(btnTheme)
        }

        btnClearData.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("确认清空")
                .setMessage("确定要清空所有习惯数据吗？此操作不可恢复。")
                .setPositiveButton("确定") { _, _ ->
                    habitStore.clearAll()
                }
                .setNegativeButton("取消", null)
                .show()
        }

        btnAbout.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("关于")
                .setMessage("${getString(R.string.app_name)}\n版本：${packageManager.getPackageInfo(packageName, 0).versionName}")
                .setPositiveButton("确定", null)
                .show()
        }

        btnBack.setOnClickListener {
            finish()
        }
    }

    private fun updateThemeButtonText(button: Button) {
        val currentKey = ThemeStore.getCurrentThemeKey(this)
        val name = ThemeStore.themeOptions.find { it.first == currentKey }?.second ?: "薄荷绿"
        button.text = "切换主题（当前：$name）"
    }

    private fun showThemePicker(button: Button) {
        val options = ThemeStore.themeOptions
        val currentKey = ThemeStore.getCurrentThemeKey(this)
        val currentIndex = options.indexOfFirst { it.first == currentKey }.coerceAtLeast(0)
        val displayNames = options.map { it.second }.toTypedArray()

        AlertDialog.Builder(this)
            .setTitle("选择主题")
            .setSingleChoiceItems(displayNames, currentIndex) { dialog, which ->
                val selectedKey = options[which].first
                if (selectedKey != currentKey) {
                    ThemeStore.setTheme(this, selectedKey)
                    updateThemeButtonText(button)
                    recreate()
                }
                dialog.dismiss()
            }
            .setNegativeButton("取消", null)
            .show()
    }
}
