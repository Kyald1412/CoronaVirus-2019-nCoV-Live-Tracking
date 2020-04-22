package co.kyald.coronavirustracking.utils.extensions

import android.animation.Animator
import android.animation.ValueAnimator
import android.content.Context
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.core.content.ContextCompat
import co.kyald.coronavirustracking.utils.SafeClickListener
import kotlinx.android.synthetic.main.marker_view.*

fun View.gone() = this.apply { visibility = View.GONE }

fun View.visible() = this.apply { visibility = View.VISIBLE }

fun View.invisible() = this.apply { visibility = View.INVISIBLE }

fun View.setSafeOnClickListener(onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun Context.color(resource: Int): Int {
    return ContextCompat.getColor(this, resource)
}

fun EditText.OnTextChangedListener(afterTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun afterTextChanged(p0: Editable?) {}

        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            p0?.let {
                afterTextChanged(editableText.toString())
            }
        }

    })
}

fun Animator.OnAnimationListener(animator: (Animator) -> Unit) {
    this.addListener(object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {
        }

        override fun onAnimationEnd(animation: Animator?) {
            animation?.let{
                animator(animation)
            }

        }

        override fun onAnimationCancel(animation: Animator?) {
        }

        override fun onAnimationStart(animation: Animator?) {
        }

    })
}