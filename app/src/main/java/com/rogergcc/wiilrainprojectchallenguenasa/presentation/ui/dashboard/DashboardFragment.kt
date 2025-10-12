package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.dashboard

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.databinding.FragmentDashboardBinding
import com.rogergcc.wiilrainprojectchallenguenasa.databinding.FragmentHomeBinding
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.setOnSingleClickListener
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.model.LocationSearch
import java.text.SimpleDateFormat

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private var _binding: FragmentDashboardBinding? = null

    private val binding get() = _binding!!
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val dateSearch = arguments?.getString("selectedDate") ?: "Select Date"
        val selectedLocation = arguments?.getParcelable<LocationSearch>("selectedLocationSearch")

        val dateFormat = SimpleDateFormat("dd-MMMM-yyyy")


        binding.dateSearch.text = dateFormat.format(selectedLocation?.selectedDate) ?: dateSearch

        binding.cardTemperatureDetails.setOnSingleClickListener() {
            findNavController().navigate(R.id.gotoDetailsView)
        }

    }

    companion object {
    }
}