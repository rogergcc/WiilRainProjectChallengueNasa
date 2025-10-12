package com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils

import android.annotation.SuppressLint
import android.app.Activity
import android.os.Build
import android.view.View
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Created on octubre.
 * year 2025 .
 */
fun Activity.hideSystemUIAndNavigation() {
    val decorView: View = this.window.decorView
    decorView.systemUiVisibility =
        (View.SYSTEM_UI_FLAG_IMMERSIVE
                // Set the content to appear under the system bars so that the
                // content doesn't resize when the system bars hide and show.
                or View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                or View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN // Hide the nav bar and status bar
                or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION
                or View.SYSTEM_UI_FLAG_FULLSCREEN)
}

@SuppressLint("NewApi")
fun Activity.adjustToolbarMarginForNotch() {
    // Notch is only supported by >= Android 9
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
        val windowInsets = this.window.decorView.rootWindowInsets
        if (windowInsets != null) {
            val displayCutout = windowInsets.displayCutout
            if (displayCutout != null) {
                val safeInsetTop = displayCutout.safeInsetTop
//                val newLayoutParams = binding.toolbar.layoutParams as ViewGroup.MarginLayoutParams
//                newLayoutParams.setMargins(0, safeInsetTop, 0, 0)
//                binding.toolbar.layoutParams = newLayoutParams
            }
        }
    }
}


fun View.setOnSingleClickListener(clickListener: View.OnClickListener?) {
    clickListener?.also {
        setOnClickListener(OnSingleClickListener(it))
    } ?: setOnClickListener(null)
}

class OnSingleClickListener(
    private val clickListener: View.OnClickListener,
    private val intervalMs: Long = 1000,
) : View.OnClickListener {
    private var canClick = AtomicBoolean(true)

    override fun onClick(v: View?) {
        if (canClick.getAndSet(false)) {
            v?.run {
                postDelayed({
                    canClick.set(true)
                }, intervalMs)
                clickListener.onClick(v)
            }
        }
    }
}