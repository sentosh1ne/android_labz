package com.labz.utils

import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Sentosh1ne on 18.10.2017.
 */
fun Int.formattedDate(): String {
    val date = Date(this.toLong() * 1000)
    val format = SimpleDateFormat("yyyy-MMMM-dd | HH:mm", Locale.US)
    return format.format(date)
}

fun Long.formattedDate(): String {
    val date = Date(this)
    val format = SimpleDateFormat("yyy MMMM dd | HH:mm", Locale.US)
    return format.format(date)
}