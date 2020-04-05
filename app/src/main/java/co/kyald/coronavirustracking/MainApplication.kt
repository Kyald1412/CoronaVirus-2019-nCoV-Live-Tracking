package co.kyald.coronavirustracking

import android.app.Application
import androidx.appcompat.app.AppCompatDelegate
import androidx.preference.PreferenceManager
import co.kyald.coronavirustracking.injection.*
import co.kyald.coronavirustracking.utils.Constants
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        initTheme()

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(
                appModule,
                networkModule,
                repositoryModule,
                viewModelModule,
                databaseModule,
                sharedPreferencesModule
            ))

        }

    }

    private fun initTheme() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)

        Timber.e("CURRENT THEME ${preferences.getString(Constants.PREF_THEME, "2")}")

        AppCompatDelegate.setDefaultNightMode(preferences.getString(Constants.PREF_THEME, "2")?.toInt()!!)
//        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
    }
}