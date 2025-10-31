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
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset
import com.rogergcc.wiilrainprojectchallenguenasa.data.weather.WeatherRepositoryAssets
import com.rogergcc.wiilrainprojectchallenguenasa.databinding.FragmentDashboardBinding
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.ClimateAnalysisResult
import com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase.AnalyzeClimateUseCase
import com.rogergcc.wiilrainprojectchallenguenasa.domain.utils.formatOneDecimal
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.DateUtils
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TEST_LOG_TAG
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
            WeatherRepositoryAssets(
                requireContext(),
            ),
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

                                        setUptColorRecomendation(analysis)

                                        staticProbability(analysis)

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

    private fun setUptColorRecomendation(analysis: ClimateAnalysisResult) {
        //                                        binding.cardRainProbability.setCardBackgroundColor(
        //                                            ContextCompat.getColor(requireContext(),
        //                                                analysis.rain.recomendation.color)
        //                                        )
        binding.cardRainProbability.strokeColor =
            ContextCompat.getColor(
                requireContext(),
                analysis.rain.recomendation.color
            )
        binding.rainEmoji.text =
            analysis.rain.recomendation.emoji

        //                                        binding.cardTemperatureProbability.setCardBackgroundColor(
        //                                            ContextCompat.getColor(requireContext(),
        //                                                analysis.temperature.recomendation.color)
        //                                        )
        binding.cardTemperatureProbability.strokeColor =
            ContextCompat.getColor(
                requireContext(),
                analysis.temperature.recomendation.color
            )
        binding.tempEmoji.text =
            analysis.temperature.recomendation.emoji

        //                                        binding.cardWindSpeedProbability.setCardBackgroundColor(
        //                                            ContextCompat.getColor(requireContext(),
        //                                                analysis.wind.recomendation.color)
        //                                        )
        binding.cardWindSpeedProbability.strokeColor =
            ContextCompat.getColor(
                requireContext(),
                analysis.wind.recomendation.color
            )

        binding.windEmoji.text =
            analysis.wind.recomendation.emoji
    }

    private fun staticProbability(analysis: ClimateAnalysisResult) {
        // Rain CardView

        binding.rainProbabilityTitle.text =
            resources.getString(analysis.rain.weatherType.description)
        binding.rainProbabilityPercentage.text =
            "${analysis.rain.probability.formatOneDecimal()}%"

        binding.rainProbabilityDescription.text =
            resources.getString(analysis.rain.recomendation.textRes)
        binding.cardRainProbability.tag =
            resources.getString(R.string.description_rain)

        // Temperature CardView
        binding.temperatureProbabilityTitle.text =
            resources.getString(analysis.temperature.weatherType.description)
        binding.temperatureProbabilityPercentage.text =
            "${analysis.temperature.average.formatOneDecimal()} ${analysis.temperature.weatherType.unit}"
        binding.temperatureProbabilityDescription.text =
            resources.getString(analysis.temperature.recomendation.textRes)
        binding.cardTemperatureProbability.tag =
            resources.getString(R.string.description_temperature)

        // Wind CardView
        binding.windSpeedTitle.text =
            resources.getString(analysis.wind.weatherType.description)
        binding.windSpeedPercentage.text =
            "${analysis.wind.average.formatOneDecimal()} ${analysis.wind.weatherType.unit}"
        binding.windSpeedDescription.text =
            resources.getString(analysis.wind.recomendation.textRes)
        binding.cardWindSpeedProbability.tag =
            resources.getString(R.string.description_wind)
    }

    private fun metadataPrint(
        datasetDummy: WeatherDataset,
        metadataDate: String?,
        firstYear: Int?,
        lastYear: Int?,
        analysis: ClimateAnalysisResult,
    ) {
        println("📍 ${datasetDummy.metadata.location} · $metadataDate") //📍 Central Park, NYC · 15 de junio
        println("📊 Datos $firstYear-$lastYear (${analysis.rain.totalYears} observaciones)")
        println()

        println("☔ LLOVIA: ${"%.1f".format(analysis.rain.probability)}%")
        println()

        println(
            "🌡 TEMPERATURA: ${"%.1f".format(analysis.temperature.average)}°C · ${
                "%.1f".format(
                    analysis.temperature.probability
                )
            }%"
        )
        println()

        println(
            "💨 VIENTO: ${"%.1f".format(analysis.wind.average)} km/h · ${
                "%.1f".format(
                    analysis.wind.probability
                )
            }%"
        )
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



    private fun listenerEvents() {
        binding.cardRainProbability.setOnSingleClickListener() {
            navigateDetails(it.tag as String)
        }
        binding.cardTemperatureProbability.setOnSingleClickListener() {
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