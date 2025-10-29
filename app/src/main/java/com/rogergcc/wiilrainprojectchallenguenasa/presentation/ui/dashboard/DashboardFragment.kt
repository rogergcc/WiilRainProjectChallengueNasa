package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.ClimateAnalysis
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRange
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRisk
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.TemperatureRange
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.WindRange
import com.rogergcc.wiilrainprojectchallenguenasa.data.weather.WeatherRepositoryAssets
import com.rogergcc.wiilrainprojectchallenguenasa.databinding.FragmentDashboardBinding
import com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase.AnalyzeClimateUseCase
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.DateUtils
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TEST_LOG_TAG
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.formatOneDecimal
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.setOnSingleClickListener
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.model.LocationSearch
import kotlinx.coroutines.launch

class DashboardFragment : Fragment(R.layout.fragment_dashboard) {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding ?: error("Binding not initialized")

    private var sendLocation: LocationSearch? = null
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    private val analyzeClimateUseCase by lazy {
        AnalyzeClimateUseCase()
    }

    private val viewModel by viewModels<DashboardResultViewModel> {
        DashboardResultViewModelFactory(
            WeatherRepositoryAssets(requireContext()),
//            calculateProbabilitiesUseCase,
            analyzeClimateUseCase
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
            viewModel.calculateProbabilities()
            viewLifecycleOwner.lifecycleScope.launch {
                viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.STARTED) {
                    viewModel.uiWeatherResult
//                    .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                        .collect { uiState ->
                            when (uiState) {
                                is DashboardResultViewModel.UiState.Loading -> {
                                    // Show loading state if needed
                                }

                                is DashboardResultViewModel.UiState.Success -> {
                                    Log.d(TEST_LOG_TAG, "Weather dataset processed successfully.")
                                    uiState.weatherDataset.let {
                                        val dateString =
                                            DateUtils.formatDayMonth(it.metadata.date.target)
                                        sendLocation = LocationSearch(

                                            selectedDateString = dateString,
                                            city = it.metadata.location.name,
                                            country = it.metadata.location.country,
                                            historicEvaluation = getString(
                                                R.string.observation_data,
                                                it.metadata.historical_context.period,
                                                it.yearly_data.count().toString()
                                            )
                                        )

                                        binding.dateSearch.text =
                                            "\uD83D\uDCCA ${sendLocation?.selectedDateString}"
                                        binding.cityCountry.text = "📍 ${it.metadata.location.name}"
                                    }
                                    uiState.analysis.let { analysis ->
                                        updateWeatherUI(
                                            analysis.rain.probability.toFloat(),
                                            analysis.temperature.average.toFloat(),
                                            analysis.wind.average.toFloat()
                                        )


                                        // Rain CardView
                                        binding.rainProbabilityTitle.text =
                                            resources.getString(analysis.rain.weatherType.description)
                                        binding.rainProbabilityPercentage.text =
                                            "${analysis.rain.probability.formatOneDecimal()}%"
                                        binding.rainProbabilityDescription.text =
                                            analysis.rain.interpretation
                                        binding.cardRainProbability.tag =
                                            resources.getString(R.string.description_rain)

                                        // Temperature CardView
                                        binding.temperatureProbabilityTitle.text =
                                            resources.getString(analysis.temperature.weatherType.description)
                                        binding.temperatureProbabilityPercentage.text =
                                            "${analysis.temperature.average.formatOneDecimal()} ${analysis.temperature.weatherType.unit}"
                                        binding.temperatureProbabilityDescription.text =
                                            analysis.temperature.interpretation
                                        binding.cardTtemperatureProbability.tag =
                                            resources.getString(R.string.description_temperature)

                                        // Wind CardView
                                        binding.windSpeedTitle.text =
                                            resources.getString(analysis.wind.weatherType.description)
                                        binding.windSpeedPercentage.text =
                                            "${analysis.wind.average.formatOneDecimal()} ${analysis.wind.weatherType.unit}"
                                        binding.windSpeedDescription.text =
                                            analysis.wind.interpretation
                                        binding.cardWindSpeedProbability.tag =
                                            resources.getString(R.string.description_wind)

                                    }

                                }

                                is DashboardResultViewModel.UiState.Failure -> {
                                    Log.e(TEST_LOG_TAG, "Error: ${uiState.errorMessage}")
                                }
                            }
                        }
                }
            }

//
//            val selectedLocation = arguments?.let {
//                BundleCompat.getParcelable(it, "selectedLocationSearch", LocationSearch::class.java)
//            }

//            metadataPrint(datasetDummy, metadataDate, firstYear, lastYear, analysis)

            // Update UI dynamically
//            optionTestOption1Ranges()

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

    private fun updateWeatherUI(rainPercentage: Float, temperature: Float, windSpeed: Float) {
        // Determine the rain range
        val rainRange = RainRange.fromValue(rainPercentage)
        println("Rain: ${rainRange.description}, Color: ${rainRange.color}")
        binding.cardRainProbability.setCardBackgroundColor(
            ContextCompat.getColor(requireContext(), rainRange.color)
        )
//        binding.cardRainProbability.strokeColor = resources.getColor(R.color.gray_500, null)

        // Determine the temperature range
        val temperatureRange = TemperatureRange.fromValue(temperature)
        println("Temperature: ${temperatureRange.description}, Color: ${temperatureRange.color}")
        binding.cardTtemperatureProbability.setCardBackgroundColor(
            ContextCompat.getColor(requireContext(), temperatureRange.color)
        )
//        binding.cardTtemperatureProbability.strokeColor = resources.getColor(temperatureRange.color, null)

        // Determine the wind range
        val windRange = WindRange.fromRange(windSpeed)
        println("Wind: ${windRange.description}, Color: ${windRange.color}")
        binding.cardWindSpeedProbability.setCardBackgroundColor(
            ContextCompat.getColor(requireContext(), windRange.color)
        )
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