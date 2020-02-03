package co.kyald.coronavirustracking.ui.feature.preferencescreen

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.OneTimeWorkRequest
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.utils.NotifyWorker
import org.koin.android.ext.android.inject
import java.util.concurrent.TimeUnit

class PreferenceActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.settings, SettingsFragment())
            .commit()

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayShowHomeEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    class SettingsFragment : PreferenceFragmentCompat() {

        private val preferences: SharedPreferences by inject()

        private fun oneTimeWorker() {
            val requestBuilder = OneTimeWorkRequest.Builder(NotifyWorker::class.java)
                .setInitialDelay(15, TimeUnit.SECONDS)
                .build()

            WorkManager.getInstance().enqueue(requestBuilder)
        }

        private fun stopAllWorker() {
            WorkManager.getInstance().cancelAllWork()
        }

        //    Schedule PeriodicWorkRequest
        private fun startNotifyWorker() {
            val requestBuilder =
                PeriodicWorkRequest.Builder(NotifyWorker::class.java, 1, TimeUnit.HOURS)
            WorkManager.getInstance().enqueueUniquePeriodicWork(
                "starterNotifyWork",
                ExistingPeriodicWorkPolicy.REPLACE,
                requestBuilder.build()
            )
        }

        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)

            // Get the switch preference
            val switchDarkMode: SwitchPreferenceCompat? = findPreference("check")

            if (preferences.getBoolean("check", false)) {
                switchDarkMode?.switchTextOn
            } else {
                switchDarkMode?.switchTextOff
            }

            // Switch preference change listener
            switchDarkMode?.setOnPreferenceChangeListener { _, newValue ->
                if (newValue == true) {
                    Toast.makeText(activity, "enabled", Toast.LENGTH_LONG).show()
                    preferences.edit().putBoolean("check", true).apply()
                    startNotifyWorker()
                } else {
                    preferences.edit().putBoolean("check", false).apply()
                    Toast.makeText(activity, "disabled", Toast.LENGTH_LONG).show()
                    stopAllWorker()
                }

                true
            }

        }

    }
}