package co.kyald.coronavirustracking.utils

import co.kyald.coronavirustracking.BuildConfig


class Constants {
    companion object {
        val S1_CORONA_API_KEY = BuildConfig.S1_CORONA_API_KEY
        val S2_CORONA_API_KEY = BuildConfig.S2_CORONA_API_KEY //Google Sheet Api
        val MAPBOX_TOKEN = BuildConfig.MAPBOX_TOKEN //Google Sheet Api

        const val PREF_LAST_UPDATE = "pref_last_update"
        const val PREF_CHECK_NOTIFICATION = "pref_key_check"
        const val PREF_DATA_SOURCE = "pref_key_datasource"

    }

    enum class DATA_SOURCE(val value: String) {
        DATA_S1("0"),
        DATA_S2("1")
    }
}