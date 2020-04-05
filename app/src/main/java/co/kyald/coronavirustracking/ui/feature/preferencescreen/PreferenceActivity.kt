package co.kyald.coronavirustracking.ui.feature.preferencescreen

import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.*
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequest
import androidx.work.WorkManager
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.utils.Constants
import co.kyald.coronavirustracking.utils.NotifyWorker
import co.kyald.coronavirustracking.utils.Utils
import org.koin.android.ext.android.inject
import timber.log.Timber
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
//        onBackPressed()
        val result = Intent()
        result.putExtra("refresh", true)
        setResult(Activity.RESULT_OK, result)
        finish()
        return true
    }

    override fun onNavigateUp(): Boolean {
//        onBackPressed()
        val result = Intent()
        result.putExtra("refresh", true)
        setResult(Activity.RESULT_OK, result)
        finish()
        return true
    }

    override fun onBackPressed() {
        val result = Intent()
        result.putExtra("refresh", true)
        setResult(Activity.RESULT_OK, result)
        finish()
    }

    class SettingsFragment : PreferenceFragmentCompat(), Preference.OnPreferenceChangeListener {

        private val preferences: SharedPreferences by inject()

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
            addPreferencesFromResource(R.xml.root_preferences)

            bindPreferenceSummaryToValue(findPreference(Constants.PREF_DATA_SOURCE)!!)

            bindPreferenceSummaryToValue(findPreference(Constants.PREF_CHECK_NOTIFICATION)!!)

            bindPreferenceSummaryToValue(findPreference(getString(R.string.corona_virus_definition))!!)

            bindPreferenceSummaryToValue(findPreference(getString(R.string.change_language))!!)

            bindPreferenceSummaryToValue(findPreference(Constants.PREF_THEME)!!)

            bindPreferenceSummaryToValue(findPreference(getString(R.string.coronavirus_prevention))!!)

            bindPreferenceSummaryToValue(findPreference(getString(R.string.about))!!)
        }

        private fun bindPreferenceSummaryToValue(preference: Preference) {

            preference.onPreferenceChangeListener = this

            if (preference is ListPreference) {
                if (preference.key == Constants.PREF_DATA_SOURCE) {
                    onPreferenceChange(
                        preference,
                        PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, Constants.DATA_SOURCE.DATA_S4.value)
                    )
                }

                if (preference.key == Constants.PREF_THEME) {
                    onPreferenceChange(
                        preference,
                        PreferenceManager
                            .getDefaultSharedPreferences(preference.context)
                            .getString(preference.key, Constants.THEME.DARK.value)
                    )
                }
            }

            if (preference is SwitchPreferenceCompat) {
                onPreferenceChange(
                    preference,
                    PreferenceManager
                        .getDefaultSharedPreferences(preference.context)
                        .getBoolean(preference.key, false)
                )
            }

        }

        override fun onPreferenceChange(preference: Preference?, value: Any?): Boolean {
            val stringValue = value.toString()


            if (preference is ListPreference) {
                if (preference.key == Constants.PREF_DATA_SOURCE) {
                    val i = preference.findIndexOfValue(value.toString())

                    if (i >= 0) {
                        preference.setValueIndex(i)
                        preference.summary = preference.entries[i].toString()

                        preferences.edit().putString(
                            Constants.PREF_DATA_SOURCE,
                            preference.entryValues[i].toString()
                        ).apply()

                    } else {
                        preference.setValueIndex(1)
                        preference.summary = preference.entries[1].toString()
                    }
                }

                if (preference.key == Constants.PREF_THEME) {
                    val i = preference.findIndexOfValue(value.toString())

                    if (i >= 0) {
                        preference.setValueIndex(i)
                        preference.summary = preference.entries[i].toString()

                        Timber.e("SET THEME ${preference.entryValues[i]}")

                        preferences.edit().putString(
                            Constants.PREF_THEME,
                            preference.entryValues[i].toString()
                        ).apply()
                    } else {
                        preference.setValueIndex(1)
                        preference.summary = preference.entries[1].toString()
                    }
                }

            } else if (preference is SwitchPreferenceCompat) {

                val boolValue = preference.isChecked

                if (boolValue) {
                    preference.switchTextOn

                } else {
                    preference.switchTextOff
                }

            } else {
                preference?.summary = stringValue
            }
            return true
        }

        override fun onPreferenceTreeClick(preference: Preference): Boolean {

            if (preference.key == getString(R.string.change_language)) {
                val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                startActivity(mIntent)
            }
            if (preference.key == getString(R.string.corona_virus_definition)) {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data =
                    Uri.parse(getString(R.string.coronavirus_definition))
                startActivity(openURL)
            }
            if (preference.key == getString(R.string.coronavirus_prevention)) {
                val openURL = Intent(Intent.ACTION_VIEW)
                openURL.data =
                    Uri.parse(getString(R.string.coronavirus_prevention))
                startActivity(openURL)
            }
            if (preference.key == getString(R.string.about)) {

                Utils().aboutAlert(
                    context!!, getString(R.string.about),
                    getString(R.string.about_message)
                )
            }

            if (preference is SwitchPreferenceCompat) {

                if (preference.isChecked) {
                    startNotifyWorker()
                    preferences.edit().putBoolean(Constants.PREF_CHECK_NOTIFICATION, true).apply()
                } else {
                    stopAllWorker()
                    preferences.edit().putBoolean(Constants.PREF_CHECK_NOTIFICATION, false).apply()

                }
            }

            return super.onPreferenceTreeClick(preference)
        }

    }
}