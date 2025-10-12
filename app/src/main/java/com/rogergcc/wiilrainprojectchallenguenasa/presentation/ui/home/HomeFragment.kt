package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.home

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.os.bundleOf
import androidx.navigation.fragment.findNavController
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.databinding.FragmentHomeBinding
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.setOnSingleClickListener
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.model.LocationSearch
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.dashboard.MaterialDatePickerFragment
import java.util.Calendar
import java.util.Date


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private var locationSearch:LocationSearch? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root

    }

    private fun showSearchBottomSheet() {
        findNavController().navigate(R.id.gotoDashboard)
//        val bottomSheet = SearchBottomSheetDialog(
//            onSearchQuery = { query ->
//                viewModel.filterJobs(query)
//            }
//        )
//        bottomSheet.show(parentFragmentManager, "SearchBottomSheet")
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cardSearchJob.setOnSingleClickListener() {
//            showSearchBottomSheet()
        }
//        binding.toolbar.visibility = View.GONE
//        viewModel.fetchLocalJobsPositions()

        binding.cardDatePickerSearch.setOnSingleClickListener() {
//            DatePickerFragment(
//                initialDate = Date(),
//                tagName = "startDate"
//            ) { year, month, day, tag ->
//                Log.d("DATE", "Seleccionaste: $day/${month + 1}/$year ($tag)")
//                binding.dateSelectedSearch.text = "$day/${month + 1}/$year"
//            }.show(parentFragmentManager, "datePicker")
//
//            MaterialDatePickerFragment(Date(), "startDate") { y, m, d, _ ->
//                Log.d("DATE", "$d/${m + 1}/$y")
//            }.show(parentFragmentManager)
//
//
//            MaterialDatePickerFragment(Date(), "endDate") { y, m, d, _ ->
//                Log.d("DATE", "$d/${m + 1}/$y")
//            }.show(parentFragmentManager)

//            MaterialDatePickerFragment(
//                onDateSelected = { year, month, day, tag ->
//                    Log.d("DATE", "Seleccionaste MaterialDat : $day/${month + 1}/$year ($tag)")
//                    binding.dateSelectedSearch.text = "$day/${month + 1}/$year"
//                }, tagName = "dateRangePicker"
//
//            ).show(parentFragmentManager)

            MaterialDatePickerFragment(
                initialDate = Date(),
                tagName = "dateSearchWeather",
                onDateSelected = { year, month, day, tag ->
                    val formatted = "%02d/%02d/%04d".format(day+1, month + 1, year)
                    Toast.makeText(
                        requireContext(),
                        "Fecha seleccionada: $formatted ($tag)",
                        Toast.LENGTH_SHORT
                    ).show()
                    binding.dateSelectedSearch.text = "${day+1}/${month + 1}/$year"

                    val selectedDate = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, day+1)
                    }.time
                    locationSearch = LocationSearch(
                        city = "Lima, PE",
                        selectedDate = Date(selectedDate.time) // Crear nueva instancia de Date
                    )

                    Log.d("DATE", "Selected Date object: cal = ${locationSearch?.selectedDate}" )

                }
            ).show(parentFragmentManager, "material_date_picker")

        }

        binding.searchButton.setOnSingleClickListener {
            val dateSearch = binding.dateSelectedSearch.text.toString()

            if (locationSearch?.selectedDate == null) {
                Toast.makeText(requireContext(), "Please select a date", Toast.LENGTH_SHORT)
                    .show()
                return@setOnSingleClickListener
            }

            val bundle = Bundle().apply {
                putString("selectedDate", dateSearch)
                putParcelable("selectedLocationSearch", locationSearch)
            }

//        findNavController().navigate(action)
            findNavController().navigate(R.id.gotoDashboard, bundle)

        }
    }

    companion object {

    }
}