package org.wit.habit

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import org.wit.habit.helpers.HabitStore

class SettingsActivity : AppCompatActivity() {

    private lateinit var habitStore: HabitStore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        habitStore = HabitStore(this)

        val tvAppName: TextView = findViewById(R.id.tvAppName)
        val tvVersion: TextView = findViewById(R.id.tvVersion)
        val btnClearData: Button = findViewById(R.id.btnClearData)
        val btnAbout: Button = findViewById(R.id.btnAbout)
        val btnBack: Button = findViewById(R.id.btnBack)

        tvAppName.text = getString(R.string.app_name)
        tvVersion.text = "版本：${packageManager.getPackageInfo(packageName, 0).versionName}"

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
}
