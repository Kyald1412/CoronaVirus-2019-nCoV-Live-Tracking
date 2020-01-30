package co.kyald.coronavirustracking.utils.extensions

import android.view.View

fun View.gone() = this.apply { visibility = View.GONE }

fun View.visible() = this.apply { visibility = View.VISIBLE }

fun View.invisible() = this.apply { visibility = View.INVISIBLE }