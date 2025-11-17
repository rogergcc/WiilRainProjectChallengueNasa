package com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.os.Build
import android.view.View
import android.widget.Toast
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.atomic.AtomicBoolean


/**
 * Created on octubre.
 * year 2025 .
 */

fun View.showView() {
    this.visibility = View.VISIBLE
}

fun View.hideView() {
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

fun Double.averageText(unit: String): String {
    return "$this $unit"
}
fun Double.probabilityText(): String {
    return "$this %"
}
fun Double.formatTwoDecimalsUsa(): String =
    DecimalFormat("#0.00", DecimalFormatSymbols(Locale.US)).format(this)




