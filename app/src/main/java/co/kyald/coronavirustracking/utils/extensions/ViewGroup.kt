package co.kyald.coronavirustracking.utils.extensions

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

fun ViewGroup.inflate(
    resId: Int,
    container: ViewGroup? = null,
    attachToRoot: Boolean = false
): View {
    return if (container == null) {
        LayoutInflater.from(this.context).inflate(resId, container)
    } else {
        LayoutInflater.from(this.context).inflate(resId, container, attachToRoot)
    }
}