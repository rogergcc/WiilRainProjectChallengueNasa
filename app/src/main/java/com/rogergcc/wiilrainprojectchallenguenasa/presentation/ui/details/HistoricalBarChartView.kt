package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.details

import com.rogergcc.wiilrainprojectchallenguenasa.data.model.YearlyData


/**
 * Created on octubre.
 * year 2025 .
 */

import android.content.Context
import android.graphics.Color
import android.view.Gravity
import android.view.View
import android.widget.*
import androidx.core.view.setPadding

fun LinearLayout.setupHistoricalBarChart(
    context: Context,
    data: List<YearlyData>,
    getValue: (YearlyData) -> Double,
    getMaxScale: (List<YearlyData>) -> Double,
    getThreshold: () -> Double,
    valueFormatter: (Double) -> String,
    unit: String
) {
    removeAllViews()
    val maxScale = getMaxScale(data)
    val threshold = getThreshold()

    data.sortedByDescending { it.year }.forEach { item ->
        val value = getValue(item)
        val percentage = (value / maxScale).coerceIn(0.0, 1.0)
        val rowLayout = LinearLayout(context).apply {
            orientation = LinearLayout.HORIZONTAL
            setPadding(4)
            gravity = Gravity.CENTER_VERTICAL
        }

        // Año
        val yearText = TextView(context).apply {
            text = "${item.year}:"
            width = dpToPx(context, 50)
            setTextColor(Color.BLACK)
        }
        rowLayout.addView(yearText)

        // Barra de fondo
        val barBackground = FrameLayout(context).apply {
            layoutParams = LinearLayout.LayoutParams(0, dpToPx(context, 20), 1f)
            setBackgroundColor(Color.parseColor("#4D000000")) // Light gray with alpha
        }

        // Barra de progreso
        val barColor =
            if (value > threshold) Color.parseColor("#F59E0B") else Color.parseColor("#10B981")

        val bar = View(context).apply {
            layoutParams = FrameLayout.LayoutParams(
                (percentage * barBackground.layoutParams.width).toInt(),
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(barColor)
        }

        // Texto dentro de la barra
        val valueText = TextView(context).apply {
            text = "${valueFormatter(value)} $unit"
            setTextColor(if (value > threshold) Color.WHITE else Color.BLACK)
            textSize = 12f
            gravity = Gravity.END or Gravity.CENTER_VERTICAL
            setPadding(dpToPx(context, 8), 0, dpToPx(context, 8), 0)
        }

        barBackground.addView(bar)
        barBackground.addView(valueText)

        rowLayout.addView(barBackground)

        addView(rowLayout)
    }
}

private fun dpToPx(context: Context, dp: Int): Int {
    return (dp * context.resources.displayMetrics.density).toInt()
}
