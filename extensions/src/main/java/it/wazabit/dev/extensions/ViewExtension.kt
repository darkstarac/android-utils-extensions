package it.wazabit.dev.extensions

import android.graphics.Paint
import android.os.Build
import android.os.SystemClock
import android.text.Html
import android.view.View
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.view.animation.BounceInterpolator
import android.view.animation.TranslateAnimation
import android.widget.TextView


/**
 * Prevent double clicks on Button or other [View] types.
 */
class SafeClickListener(
    private val intervalMillis: Int = 1000,
    private val onSafeCLick: (View) -> Unit
) : View.OnClickListener {
    private var lastTimeClicked: Long = 0
    override fun onClick(v: View) {
        if (SystemClock.elapsedRealtime() - lastTimeClicked < intervalMillis) {
            return
        }
        lastTimeClicked = SystemClock.elapsedRealtime()
        onSafeCLick(v)
    }
}

fun View.setSafeOnClickListener(intervalMillis: Int = 1000, onSafeClick: (View) -> Unit) {
    val safeClickListener = SafeClickListener(intervalMillis) {
        onSafeClick(it)
    }
    setOnClickListener(safeClickListener)
}

fun View.visible() {
    this.visibility = View.VISIBLE
}

fun View.invisible() {
    this.visibility = View.INVISIBLE
}

fun View.gone() {
    this.visibility = View.GONE
}

fun View.show(show: Boolean) {
    this.visibility = when (show) {
        true -> View.VISIBLE
        else -> View.GONE
    }
}

fun View.hide(show: Boolean) {
    this.visibility = when (show) {
        true -> View.VISIBLE
        else -> View.INVISIBLE
    }
}

fun View.slideUp(aDuration: Int = resources.getInteger(android.R.integer.config_shortAnimTime)) {
    visibility = View.INVISIBLE
    post {
        val animate = TranslateAnimation(0f, 0f, this.height.toFloat(), 0f).apply {
            duration = aDuration.toLong()
            fillAfter = true
            interpolator = BounceInterpolator()
        }
        this.startAnimation(animate)
    }
}

fun View.slideDown(aDuration: Int = resources.getInteger(android.R.integer.config_shortAnimTime)) {
    visibility = View.VISIBLE
    val animate = TranslateAnimation(0f, 0f, 0f, this.height.toFloat()).apply {
        duration = aDuration.toLong()
        fillAfter = true
        interpolator = BounceInterpolator()
    }
    this.startAnimation(animate)
}

fun View.showFade(aDuration: Int = resources.getInteger(android.R.integer.config_shortAnimTime)) {
    visibility = View.VISIBLE
    val animate = AlphaAnimation(0.0f, 1.0f).apply {
        duration = aDuration.toLong()
        fillAfter = true
    }
    this.startAnimation(animate)
}

fun View.hideFade(
    aDuration: Int = resources.getInteger(android.R.integer.config_shortAnimTime),
    listener: Animation.AnimationListener? = null
) {
    visibility = View.VISIBLE
    val animate = AlphaAnimation(1.0f, 0.0f).apply {
        duration = aDuration.toLong()
        setAnimationListener(object : Animation.AnimationListener {
            override fun onAnimationStart(animation: Animation?) {
            }

            override fun onAnimationEnd(animation: Animation?) {
                visibility = View.GONE
                listener?.onAnimationEnd(animation)
            }

            override fun onAnimationRepeat(animation: Animation?) {
            }
        })
    }
    this.startAnimation(animate)
}

fun TextView.markdown() {
    paintFlags = paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
}

fun TextView.setHtmlText(text: String) {
    setText(
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            Html.fromHtml(text, Html.FROM_HTML_MODE_LEGACY)
        } else {
            Html.fromHtml(text)
        }
    )
}