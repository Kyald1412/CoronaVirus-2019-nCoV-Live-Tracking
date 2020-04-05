package co.kyald.coronavirustracking.utils

import androidx.appcompat.app.AppCompatDelegate
import co.kyald.coronavirustracking.BuildConfig


class Constants {
    companion object {
        const val MAPBOX_TOKEN = BuildConfig.MAPBOX_TOKEN //Google Sheet Api

        const val PREF_LAST_UPDATE = "pref_last_update"
        const val PREF_CHECK_NOTIFICATION = "pref_key_check"
        const val PREF_DATA_SOURCE = "pref_key_datasource"
        const val PREF_THEME = "pref_key_theme"

    }

    enum class DATA_SOURCE(val value: String) {
        DATA_S1("0"),
        DATA_S2("1"),
        DATA_S3("2"),
        DATA_S4("3")
    }


    enum class THEME(val value: String) {
        LIGHT(AppCompatDelegate.MODE_NIGHT_NO.toString()),
        DARK(AppCompatDelegate.MODE_NIGHT_YES.toString())
    }
}