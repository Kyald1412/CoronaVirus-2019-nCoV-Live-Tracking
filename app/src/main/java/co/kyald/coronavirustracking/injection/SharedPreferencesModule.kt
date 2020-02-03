package co.kyald.coronavirustracking.injection

import android.content.Context
import android.content.SharedPreferences
import android.preference.PreferenceManager
import org.koin.dsl.module

val sharedPreferencesModule = module {
    single { provideSharedPreferences(get()) }
}

private fun provideSharedPreferences(context: Context): SharedPreferences {
    return PreferenceManager.getDefaultSharedPreferences(context)
}