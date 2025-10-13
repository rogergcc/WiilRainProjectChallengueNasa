package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.WeatherDataManager
import com.rogergcc.wiilrainprojectchallenguenasa.databinding.FragmentDashboardBinding
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.DateUtils
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.setOnSingleClickListener
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.toast
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.model.LocationSearch
import java.util.Date

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
        try {
            val dataManager = WeatherDataManager(requireContext())
            val forecastResponse = dataManager.getWeatherData()

            val dataAqpSearchDummy = dataManager.parseWeatherData()

//        val forecastResponse = Gson().fromJson(dummyJsonString, ForecastResponse::class.java)

            // Display the result
            if (forecastResponse.forecast.will_rain) {
//            tvResult.text = "¡SÍ, LLEVA PARAGUAS! 🌧️"
//            Toast.makeText(requireContext(), "Si lleva paragyar \uFE0F", Toast.LENGTH_SHORT).show()
                requireContext().toast("¡SÍ, LLEVA PARAGUAS! 🌧️")
            } else {
                requireContext().toast("No lleva paraguas ☀ \uFE0F")

            }
            val rainDataVariable = dataAqpSearchDummy.variables?.get(0)
            binding.rainProbabilityTitle.text = rainDataVariable?.name ?: "Rain Probability"
            binding.rainProbabilityPercentage.text = "${rainDataVariable?.value ?: 0} ${rainDataVariable?.unit ?: "%"}"
            binding.rainProbabilityDescription.text = rainDataVariable?.description
            binding.cardRainProbability.tag = rainDataVariable?.id

            val tempDataVariable = dataAqpSearchDummy.variables?.get(1)
            binding.temperatureProbabilityTitle.text = tempDataVariable?.name ?: "Temperature"
            binding.temperatureProbabilityPercentage.text = "${tempDataVariable?.value ?: 0} ${tempDataVariable?.unit ?: "°C"}"
            binding.temperatureProbabilityDescription.text = tempDataVariable?.description
            binding.cardTtemperatureProbability.tag = tempDataVariable?.id

            val windDataVariable = dataAqpSearchDummy.variables?.get(2)
            binding.windSpeedTitle.text = windDataVariable?.name ?: "Wind Speed"
            binding.windSpeedPercentage.text = "${windDataVariable?.value ?: 0} ${windDataVariable?.unit ?: "km/h"}"
            binding.windSpeedDescription.text = windDataVariable?.description
            binding.cardWindSpeedProbability.tag = windDataVariable?.id


        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error: ${e.message}")
            e.printStackTrace()
        }


        val dateSearch = arguments?.getString("selectedDate") ?: "Select Date"
//        val selectedLocation = arguments?.getParcelable<LocationSearch>("selectedLocationSearch")

        val selectedLocation = arguments?.let {
            BundleCompat.getParcelable(it, "selectedLocationSearch", LocationSearch::class.java)
        }

        val currentDate = selectedLocation?.selectedDate ?: Date()
        val formattedDate = DateUtils.format(currentDate)

        binding.dateSearch.text = formattedDate
        binding.cityCountry.text = selectedLocation?.city

//        binding.cardRainProbability.setOnSingleClickListener() {
//            findNavController().navigate(R.id.gotoDetailsView)
//        }

    }

    companion object {
    }
}