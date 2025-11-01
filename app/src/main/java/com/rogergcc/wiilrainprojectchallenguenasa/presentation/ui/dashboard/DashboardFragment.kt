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
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.Recommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.TemperatureRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.WindRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.weather.WeatherRepositoryAssets
import com.rogergcc.wiilrainprojectchallenguenasa.databinding.FragmentDashboardBinding
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.ClimateAnalysisResult
import com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase.AnalyzeClimateUseCase
import com.rogergcc.wiilrainprojectchallenguenasa.domain.utils.formatOneDecimal
import com.rogergcc.wiilrainprojectchallenguenasa.domain.utils.formatOneDecimalLocale
import com.rogergcc.wiilrainprojectchallenguenasa.domain.utils.formatTwoDecimalLocale
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.BUNDLE_LOCATION_SEARCH
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
                                    val rainDataLevel =
                                        RainRecommendation.getRecommendation(analysis.rain.probability.toFloat())
                                    val temperatureDataLevel =
                                        TemperatureRecommendation.getRecommendation(analysis.temperature.average.toFloat())
                                    val windDataLevel =
                                        WindRecommendation.getRecommendation(analysis.wind.average.toFloat())

                                    setUptColorRecomendation(
                                        analysis,
                                        rainDataLevel,
                                        temperatureDataLevel,
                                        windDataLevel
                                    )
                                    staticProbability(
                                        analysis,
                                        rainDataLevel,
                                        temperatureDataLevel,
                                        windDataLevel
                                    )

                                }


                            }

                            is DashboardResultViewModel.UiState.Failure -> {
                                Log.e(TEST_LOG_TAG, "Error: ${uiState.errorMessage}")
                            }
                        }
                    }
            }
        }
//            val selectedLocation = arguments?.let {
//                BundleCompat.getParcelable(it, BUNDLE_LOCATION_SEARCH, LocationSearch::class.java)
//            }
        listenerEvents()

    }

    private fun setUptColorRecomendation(
        analysis: ClimateAnalysisResult,
        rainDataLevel: Recommendation,
        temperaturaDataLevel: TemperatureRecommendation,
        windDataLevel: WindRecommendation,
    ) {
        //                                        binding.cardRainProbability.setCardBackgroundColor(
        //                                            ContextCompat.getColor(requireContext(),
        //                                                analysis.rain.recomendation.color)
        //                                        )

        binding.cardRainProbability.strokeColor =
            ContextCompat.getColor(
                requireContext(),
                rainDataLevel.color
            )
        binding.rainEmoji.text =
            rainDataLevel.emoji

        //                                        binding.cardTemperatureProbability.setCardBackgroundColor(
        //                                            ContextCompat.getColor(requireContext(),
        //                                                analysis.temperature.recomendation.color)
        //                                        )
        binding.cardTemperatureProbability.strokeColor =
            ContextCompat.getColor(
                requireContext(),
                temperaturaDataLevel.color
            )
        binding.tempEmoji.text =
            temperaturaDataLevel.emoji

        //                                        binding.cardWindSpeedProbability.setCardBackgroundColor(
        //                                            ContextCompat.getColor(requireContext(),
        //                                                analysis.wind.recomendation.color)
        //                                        )
        binding.cardWindSpeedProbability.strokeColor =
            ContextCompat.getColor(
                requireContext(),
                windDataLevel.color
            )

        binding.windEmoji.text =
            windDataLevel.emoji
    }

    private fun staticProbability(
        analysis: ClimateAnalysisResult,
        rainDataLevel: Recommendation,
        temperatureDataLevel: TemperatureRecommendation,
        windDataLevel: WindRecommendation,
    ) {
        // Rain CardView
        binding.rainProbabilityTitle.text =
            resources.getString(analysis.rain.weatherType.description)
        binding.rainProbabilityPercentage.text =
            "${analysis.rain.probability.formatOneDecimalLocale()}%"

        binding.rainProbabilityDescription.text =
            resources.getString(rainDataLevel.descRes)
        binding.cardRainProbability.tag =
            resources.getString(R.string.description_rain)

        // Temperature CardView
        binding.temperatureProbabilityTitle.text =
            resources.getString(analysis.temperature.weatherType.description)
        binding.temperatureProbabilityPercentage.text =
            "${analysis.temperature.average.formatOneDecimalLocale()} ${analysis.temperature.weatherType.unit}"
        binding.temperatureProbabilityDescription.text =
            resources.getString(temperatureDataLevel.descRes)
        binding.cardTemperatureProbability.tag =
            resources.getString(R.string.description_temperature)

        // Wind CardView
        binding.windSpeedTitle.text =
            resources.getString(analysis.wind.weatherType.description)
        binding.windSpeedPercentage.text =
            "${analysis.wind.average.formatOneDecimal()} ${analysis.wind.weatherType.unit}"
        binding.windSpeedDescription.text =
            resources.getString(windDataLevel.descRes)
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

        println("☔ ${analysis.rain.weatherType.name}: ${analysis.rain.probability.formatOneDecimalLocale()}%")
        println()

        println(
            "🌡 ${analysis.temperature.weatherType.name}: ${analysis.temperature.average.formatOneDecimalLocale()}${analysis.temperature.weatherType.unit} · ${
                analysis.temperature.probability

            }%"
        )
        println()

        println(
            "💨 ${analysis.wind.weatherType.name}: ${analysis.wind.average.formatTwoDecimalLocale()} ${analysis.wind.weatherType.unit} · ${
                analysis.wind.probability
            }%"
        )
        Log.e(TEST_LOG_TAG, "---------------------------")
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
            it.putParcelable(BUNDLE_LOCATION_SEARCH, sendLocation)

        })

    }

    companion object {
    }
}