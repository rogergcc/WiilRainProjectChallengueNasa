package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.dashboard

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.os.BundleCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.RainRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.Recommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.TemperatureRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges.WindRecommendation
import com.rogergcc.wiilrainprojectchallenguenasa.data.weather.WeatherRepositoryAssets
import com.rogergcc.wiilrainprojectchallenguenasa.databinding.FragmentDashboardBinding
import com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase.AnalyzeClimateUseCase
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.BUNDLE_LOCATION_SEARCH
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.DateUtils
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TEST_LOG_TAG
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.averageText
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.hideView
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.probabilityText
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.setOnSingleClickListener
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.showView
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.toast
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.model.DashboardUiState
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
//
    }

    private val analyzeClimateUseCase by lazy {
        AnalyzeClimateUseCase()
    }

    private val viewModel by viewModels<DashboardResultViewModel> {
        DashboardResultViewModelFactory(
            WeatherRepositoryAssets(
                requireContext(),
            ),
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

    private fun hideLoadingState() {
        binding.swipeRefresh.isRefreshing = false
        binding.simmerDashboard.hideView()

        binding.containterDashboard.showView()
        binding.simmerDashboard.stopShimmer()
    }

    private fun showLoadingState() {
        binding.swipeRefresh.isRefreshing = true
        binding.containterDashboard.hideView()

        binding.simmerDashboard.showView()
        binding.simmerDashboard.startShimmer()

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel.calculateProbabilities()

        binding.swipeRefresh.setOnRefreshListener {
            viewModel.calculateProbabilities()
        }

        val selectedLocation = arguments?.let {
            BundleCompat.getParcelable(it, BUNDLE_LOCATION_SEARCH, LocationSearch::class.java)
        }
        sendLocation = selectedLocation
        val dateTitle =  DateUtils.formatDayMonthYear(selectedLocation?.selectedDateString ?: "")
        val cityCountryTitle = "${selectedLocation?.city}, ${selectedLocation?.country}"

        binding.cityCountry.text = "📍 $cityCountryTitle"
        binding.dateSearch.text = "📆 $dateTitle"

        Log.d(TEST_LOG_TAG, "Selected location from bundle: $selectedLocation")
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(androidx.lifecycle.Lifecycle.State.STARTED) {

                viewModel.uiWeatherResult
//                    .flowWithLifecycle(lifecycle, Lifecycle.State.CREATED)
                    .collect { uiState ->
                        when (uiState) {
                            is DashboardResultViewModel.UiState.Loading -> {
                                showLoadingState()
                            }
                            is DashboardResultViewModel.UiState.Success -> {
                                hideLoadingState()
                                Log.d(TEST_LOG_TAG, "Weather dataset processed successfully.")
                                requireContext().toast("[Dashboard] Data loaded successfully.")
                                uiState.uiState.let {
//                                    val dateString = DateUtils.formatDayMonth(it.metadata.date.target)

                                    sendLocation = (sendLocation ?: LocationSearch()).copy(
                                        historicEvaluation = getString(
                                            R.string.observation_data,
                                            it.periodYearsObservation,
                                            it.countObservations,
                                        )
                                    )

                                    val rainDataLevel =
                                        RainRecommendation.getRecommendation(it.rain.probability.toFloat())
                                    val temperatureDataLevel =
                                        TemperatureRecommendation.getRecommendation(it.temperature.average.toFloat())
                                    val windDataLevel =
                                        WindRecommendation.getRecommendation(it.wind.average.toFloat())

                                    setUpRecommendation(
                                        rainDataLevel,
                                        temperatureDataLevel,
                                        windDataLevel
                                    )
                                    staticProbability(
                                        it,
                                        rainDataLevel,
                                        temperatureDataLevel,
                                        windDataLevel
                                    )

                                }

                            }

                            is DashboardResultViewModel.UiState.Failure -> {
                                hideLoadingState()
                                Log.e(TEST_LOG_TAG, "Error: ${uiState.errorMessage}")
                            }
                        }
                    }
            }
        }

        listenerEvents()

    }

    private fun setUpRecommendation(
        rainDataLevel: Recommendation,
        temperatureDataLevel: TemperatureRecommendation,
        windDataLevel: WindRecommendation,
    ) {
        binding.cardRainProbability.strokeColor =
            ContextCompat.getColor(
                requireContext(),
                rainDataLevel.color
            )
        binding.rainEmoji.text =
            rainDataLevel.emoji

        binding.cardTemperatureProbability.strokeColor =
            ContextCompat.getColor(
                requireContext(),
                temperatureDataLevel.color
            )
        binding.tempEmoji.text =
            temperatureDataLevel.emoji

        binding.cardWindSpeedProbability.strokeColor =
            ContextCompat.getColor(
                requireContext(),
                windDataLevel.color
            )

        binding.windEmoji.text =
            windDataLevel.emoji
    }

    private fun staticProbability(
        state: DashboardUiState,
        rainDataLevel: Recommendation,
        temperatureDataLevel: TemperatureRecommendation,
        windDataLevel: WindRecommendation,
    ) {

        // Rain
        binding.rainProbabilityTitle.text =
            resources.getString(state.rain.weatherType.description)
        binding.rainProbabilityPercentage.text =
            state.rain.probability.probabilityText()

        binding.rainProbabilityDescription.text =
            resources.getString(rainDataLevel.descRes)
        binding.cardRainProbability.tag =
            resources.getString(R.string.description_rain)

        // Temperature
        binding.temperatureProbabilityTitle.text =
            resources.getString(state.temperature.weatherType.description)
        binding.temperatureProbabilityPercentage.text =
            state.temperature.average.averageText(state.temperature.weatherType.unit)
        binding.temperatureProbabilityDescription.text =
            resources.getString(temperatureDataLevel.descRes)
        binding.cardTemperatureProbability.tag =
            resources.getString(R.string.description_temperature)

        // Wind
        binding.windSpeedTitle.text =
            resources.getString(state.wind.weatherType.description)
        binding.windSpeedPercentage.text =
            state.wind.average.averageText(state.wind.weatherType.unit)
        binding.windSpeedDescription.text =
            resources.getString(windDataLevel.descRes)
        binding.cardWindSpeedProbability.tag =
            resources.getString(R.string.description_wind)
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

        sendLocation?.type = tag

        findNavController().navigate(R.id.gotoDetailsView, Bundle().also {
            it.putParcelable(BUNDLE_LOCATION_SEARCH, sendLocation)

        })

    }

    companion object {
    }
}