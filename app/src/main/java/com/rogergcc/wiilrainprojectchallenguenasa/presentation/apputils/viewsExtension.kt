package com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.Toast
import java.text.SimpleDateFormat
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Created on octubre.
 * year 2025 .
 */

fun View.show() {
    this.visibility = View.VISIBLE
}
fun View.hide() {
    this.visibility = View.GONE
}
fun View.invisible() {
    this.visibility = View.INVISIBLE
}
fun Toast.makeTextShort(message: String, activity: Activity) {
    Toast.makeText(activity, message, Toast.LENGTH_SHORT).show()
}

fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}

fun Double.formatTwoDecimals():String{
    return DECIMAL_FORMAT_TWO.format(this)
}
fun Double.formatOneDecimal():String{
    return DECIMAL_FORMAT_ONE.format(this)
}
