package org.wit.habit

import android.app.Application
import timber.log.Timber

class HabitApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        Timber.plant(Timber.DebugTree())
    }
}
