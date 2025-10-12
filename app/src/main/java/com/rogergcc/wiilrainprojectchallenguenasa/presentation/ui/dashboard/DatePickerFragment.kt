package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.dashboard


/**
 * Created on octubre.
 * year 2025 .
 */
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Bundle
import android.widget.DatePicker
import androidx.fragment.app.DialogFragment
import java.util.Calendar
import java.util.Date

class DatePickerFragment(
    private val initialDate: Date = Date(),
    private val tagName: String? = null,
    private val onDateSelected: (year: Int, month: Int, day: Int, tag: String?) -> Unit,
) : DialogFragment(), DatePickerDialog.OnDateSetListener {


    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val c = Calendar.getInstance().apply { time = initialDate }
        return DatePickerDialog(
            requireContext(),
            this,
            c.get(Calendar.YEAR),
            c.get(Calendar.MONTH),
            c.get(Calendar.DAY_OF_MONTH)
        )
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, day: Int) {
        onDateSelected(year, month, day, tagName)
    }
}
