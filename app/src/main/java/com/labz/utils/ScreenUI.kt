package com.labz.utils

import android.app.Activity
import android.util.DisplayMetrics
import android.view.View
import android.view.ViewGroup


fun Activity.screenWidth(): Int {
    val metrics = DisplayMetrics()
    this.windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.widthPixels
}

fun Activity.screenHeight(): Int {
    val metrics = DisplayMetrics()
    this.windowManager.defaultDisplay.getMetrics(metrics)
    return metrics.heightPixels
}

fun View.updateMargins(top: Int, left: Int, right: Int, bottom: Int) {
    if (this.layoutParams is ViewGroup.MarginLayoutParams) {
        val p = this.layoutParams as (ViewGroup.MarginLayoutParams)
        p.setMargins(p.leftMargin + left, p.topMargin + top,
                p.rightMargin + right, p.bottomMargin + bottom)
        this.requestLayout()
    }

}

