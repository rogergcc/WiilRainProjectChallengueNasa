package com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils

import android.app.Activity
import android.content.Context
import android.view.View
import android.widget.Toast
import com.rogergcc.wiilrainprojectchallenguenasa.domain.utils.formatOneDecimalLocale
import java.text.DecimalFormat
import java.text.DecimalFormatSymbols
import java.util.Locale


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
    return "${this.formatOneDecimalLocale()} $unit"
}

fun Double.probabilityText(): String {
    return "${this.formatOneDecimalLocale()}%"
}

fun Double.formatTwoDecimalsUsa(): String =
    DecimalFormat("#0.00", DecimalFormatSymbols(Locale.US)).format(this)




