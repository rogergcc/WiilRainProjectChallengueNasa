package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.ClimateAnalysis
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRange
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRisk
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.TemperatureRange
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.WindRange
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.WeatherDataManager
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset
import com.rogergcc.wiilrainprojectchallenguenasa.data.weather.WeatherRepositoryImpl
import com.rogergcc.wiilrainprojectchallenguenasa.databinding.FragmentDashboardBinding
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.DateUtils
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TEST_LOG_TAG
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.setOnSingleClickListener
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.model.LocationSearch
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.details.WeatherDetailViewModel
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.details.WeatherDetailViewModelFactory
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private var _binding: FragmentDashboardBinding? = null
    private var sendLocation: LocationSearch? = null

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

    private val viewModel by viewModels<DashboarResultViewModel> {
        DashboarResultViewModelFactory(
            WeatherRepositoryImpl(requireContext())
        )
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

            viewModel.calculateProbabilities()
            viewLifecycleOwner.lifecycleScope.launch {
                viewModel.uiWeatherResult.collect { uiState ->
                    when (uiState) {
                        is DashboarResultViewModel.DetailUiState.Loading -> {
                            // Show loading state if needed
                        }
                        is DashboarResultViewModel.DetailUiState.Success -> {
                            Log.d("TEST_LOGGER", "Weather dataset processed successfully.")

//                            val inputDateFormat = SimpleDateFormat("dd-MM", Locale.getDefault())
//                            val outputFormat = SimpleDateFormat("dd 'de' MMMM", Locale("es"))

                            uiState.weatherDataset?.let {
                                val dateString = DateUtils.formatDayMonth(it.metadata.date.target)
                                sendLocation = LocationSearch(

                                    selectedDateString = dateString,
                                    city = it.metadata.location.name,
                                    country = it.metadata.location.country,
                                    historicEvaluation = getString(
                                        R.string.datos_observaciones,
                                        it.metadata.historical_context.period,
                                        it.yearly_data.count().toString()
                                    )
                                )
                                Log.d("TEST_LOGGER", "Dataset Metadata Location: ${it.metadata.location.name}, Country: ${it.metadata.location.country}")

                                binding.dateSearch.text = "\uD83D\uDCCA ${sendLocation?.selectedDateString }"
                                binding.cityCountry.text = "📍 ${it.metadata.location.name}"

                            }
                        }
                        is DashboarResultViewModel.DetailUiState.Failure -> {
                            Log.e(TEST_LOG_TAG, "Error: ${uiState.errorMessage}")
                        }
                    }
                }
            }
            val dataManager = WeatherDataManager(requireContext())
            val datasetDummy = dataManager.parseWeatherDataset()


            val probabilities = dataManager.calculateProbabilities(datasetDummy.yearly_data)
            Log.d("TEST_LOGGER", "Probabilities: $probabilities")

            val analysis = dataManager.analyzeClimateFromDataset(datasetDummy.yearly_data)


//            val firstYear = datasetDummy.yearly_data.minByOrNull { it.year }?.year
//            val lastYear = datasetDummy.yearly_data.maxByOrNull { it.year }?.year
//
//            val selectedLocation = arguments?.let {
//                BundleCompat.getParcelable(it, "selectedLocationSearch", LocationSearch::class.java)
//            }

//            metadataPrint(datasetDummy, metadataDate, firstYear, lastYear, analysis)

            // Update UI dynamically
//            optionTestOption1Ranges()

            updateWeatherUI(analysis.rain.probability.toFloat(),
                analysis.temperature.averageTemperature.toFloat(),
                analysis.wind.averageWind.toFloat())

//            binding.dateSearch.text = period  // Example: "1985-2024"
//            binding.cityCountry.text = datasetDummy.metadata.location.name // Example: "New York Central Park"

            // Rain CardView
            binding.rainProbabilityTitle.text = "Lluvia"
            binding.rainProbabilityPercentage.text = "${"%.1f".format(analysis.rain.probability)}%"
            binding.rainProbabilityDescription.text = "${analysis.rain.interpretation} "
//            binding.rainVisualRepresentation.text = "${analysis.rain.visualBar}"
            binding.cardRainProbability.tag = "rain"

            // Temperature CardView
            binding.temperatureProbabilityTitle.text = "Temperatura"
            binding.temperatureProbabilityPercentage.text = "${"%.1f".format(analysis.temperature.averageTemperature)}°C"
//            binding.temperatureProbabilityPercentage.text = "${analysis.temperature.minTemperature}°C-${analysis.temperature.maxTemperature}°C"
            binding.temperatureProbabilityDescription.text = "${analysis.temperature.interpretation}"
            binding.temperatureVisualRepresentation.text = "${analysis.temperature.interpretation}"
            binding.cardTtemperatureProbability.tag = "temperature"

            // Wind CardView
            binding.windSpeedTitle.text = "Viento"
            binding.windSpeedPercentage.text = "${"%.1f".format(analysis.wind.averageWind)} km/h"
            binding.windSpeedDescription.text = "${analysis.wind.interpretation}"
//            binding.windVisualRepresentation.text = "${analysis.wind.visualScale}"
            binding.cardWindSpeedProbability.tag = "wind"



        } catch (e: Exception) {
            Log.e("DashboardFragment", "Error: ${e.message}")
            e.printStackTrace()
        }


        listenerEvents()

    }

    private fun metadataPrint(
        datasetDummy: WeatherDataset,
        metadataDate: String?,
        firstYear: Int?,
        lastYear: Int?,
        analysis: ClimateAnalysis,
    ) {
        println("📍 ${datasetDummy.metadata.location} · $metadataDate") //📍 Central Park, NYC · 15 de junio
        println("📊 Datos $firstYear-$lastYear (${analysis.rain.totalYears} observaciones)")
        println()

        println("☔ LLOVIA: ${"%.1f".format(analysis.rain.probability)}%")
//        println(analysis.rain.visualBar)
        println("\"${analysis.rain.interpretation}\"")
        println()

        println(
            "🌡 TEMPERATURA: ${"%.1f".format(analysis.temperature.averageTemperature)}°C · ${
                "%.1f".format(
                    analysis.temperature.heatProbability
                )
            }%"
        )
        println("\"${analysis.temperature.interpretation}\"")
        println()

        println(
            "💨 VIENTO: ${"%.1f".format(analysis.wind.averageWind)} km/h · ${
                "%.1f".format(
                    analysis.wind.strongWindProbability
                )
            }%"
        )
        println("\"${analysis.wind.interpretation}\"")

        // Metadata adicional
        println("\n--- METADATA ---")

//        println("Período analizado: ${analysis.metadata["historical_period"]}")
//        println("Umbral lluvia: ${(analysis.metadata["thresholds_used"] as Map<*, *>)["rain"]} mm")
//        println("Umbral calor: ${(analysis.metadata["thresholds_used"] as Map<*, *>)["extreme_heat"]} °C")
//        println("Umbral viento: ${(analysis.metadata["thresholds_used"] as Map<*, *>)["strong_wind"]} km/h")
//        println("\n--- METADATA 2 ---")
//        println("Metada 2--- ${analysis.metadata}")

        Log.e("DashboardFragment", "---------------------------")
    }

    fun updateWeatherUI(rainPercentage: Float, temperature: Float, windSpeed: Float) {

        // Determine the rain range
        val rainRange = RainRange.fromValue(rainPercentage)
        println("Rain: ${rainRange.description}, Color: ${rainRange.color}")
        binding.cardRainProbability.setCardBackgroundColor(resources.getColor(rainRange.color, null))
//        binding.cardRainProbability.strokeColor = resources.getColor(R.color.gray_500, null)

        // Determine the temperature range
        val temperatureRange = TemperatureRange.fromValue(temperature)
        println("Temperature: ${temperatureRange.description}, Color: ${temperatureRange.color}")
        binding.cardTtemperatureProbability.setCardBackgroundColor(resources.getColor(temperatureRange.color, null))
//        binding.cardTtemperatureProbability.strokeColor = resources.getColor(temperatureRange.color, null)

        // Determine the wind range
        val windRange = WindRange.fromRange(windSpeed)
        println("Wind: ${windRange.description}, Color: ${windRange.color}")
        binding.cardWindSpeedProbability.setCardBackgroundColor(resources.getColor(windRange.color, null))
//        binding.cardWindSpeedProbability.strokeColor = resources.getColor(windRange.color, null)

    }

    fun optionTestOption1Ranges() {
        binding.cardRainProbability.setCardBackgroundColor(RainRange.HIGH.color)
        binding.cardTtemperatureProbability.setCardBackgroundColor(TemperatureRange.COMFORT.color)
        binding.cardWindSpeedProbability.setCardBackgroundColor(WindRange.MODERATE.color)
    }

    fun optionTestOption2Ranges() {
        binding.cardRainProbability.setCardBackgroundColor(RainRange.LOW.color)
        binding.cardTtemperatureProbability.setCardBackgroundColor(TemperatureRange.COMFORT.color)
        binding.cardWindSpeedProbability.setCardBackgroundColor(RainRisk.MODERATE.color)
    }


    private fun listenerEvents() {
        binding.cardRainProbability.setOnSingleClickListener() {
            navigateDetails(it.tag as String)
        }
        binding.cardTtemperatureProbability.setOnSingleClickListener() {
            navigateDetails(it.tag as String)
        }
        binding.cardWindSpeedProbability.setOnSingleClickListener() {
            navigateDetails(it.tag as String)
        }
    }

    private fun navigateDetails(tag: String) {
//        findNavController().navigate(R.id.gotoDetailsView, Bundle().also {
//            it.putString("selectedValue", tag)
//
//        })
        sendLocation?.type = tag

        findNavController().navigate(R.id.gotoDetailsView, Bundle().also {
            it.putParcelable("selectedLocationSearch", sendLocation)

        })

    }

    companion object {
    }
}