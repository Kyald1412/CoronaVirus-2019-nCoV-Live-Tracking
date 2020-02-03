package co.kyald.coronavirustracking.utils.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import java.text.SimpleDateFormat
import java.util.*


fun Date.toSimpleString() : String {
    val format = SimpleDateFormat("dd MMM yyy, EEEE HH:mm:ss", Locale.ENGLISH)
    return format.format(this)
}