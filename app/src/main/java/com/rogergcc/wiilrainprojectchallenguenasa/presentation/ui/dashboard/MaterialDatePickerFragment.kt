package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.dashboard

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.FragmentManager
import com.google.android.material.datepicker.MaterialDatePicker
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TEST_LOG_TAG
import java.util.Calendar
import java.util.Date


/**
 * Created on octubre.
 * year 2025 .
 */
class MaterialDatePickerFragment(
    private val initialDate: Date? = null,
    private val tagName: String? = null,
    private val onDateSelected: (year: Int, month: Int, day: Int, tag: String?) -> Unit
) : DialogFragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val calendar = Calendar.getInstance().apply {
            time = initialDate ?: Date()
        }

        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText("Select date")
            .setSelection(calendar.timeInMillis)
            .build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            val cal = Calendar.getInstance().apply { timeInMillis = selection }
            val year = cal.get(Calendar.YEAR)
            val month = cal.get(Calendar.MONTH)
            val day = cal.get(Calendar.DAY_OF_MONTH)
            onDateSelected(year, month, day, tagName)
            dismiss()
        }
        datePicker.addOnNegativeButtonClickListener() {
            Log.d(TEST_LOG_TAG, "[DatePicker] Negative button clicked")
            dismiss()
        }
        datePicker.addOnDismissListener() {
            Log.d(TEST_LOG_TAG, "[DatePicker] Dialog dismissed")
            dismiss()
        }
        datePicker.addOnCancelListener() {
            Log.d(TEST_LOG_TAG, "[DatePicker] Dialog canceled")
            dismiss()
        }


        datePicker.show(parentFragmentManager, "material_date_picker")
    }
}