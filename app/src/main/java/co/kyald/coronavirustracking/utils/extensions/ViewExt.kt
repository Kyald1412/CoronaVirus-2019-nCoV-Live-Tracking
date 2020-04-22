package co.kyald.coronavirustracking.utils.extensions

import android.view.View
import co.kyald.coronavirustracking.utils.SafeClickListener

fun View.gone() = this.apply { visibility = View.GONE }

fun View.visible() = this.apply { visibility = View.VISIBLE }

fun View.invisible() = this.apply { visibility = View.INVISIBLE }

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}