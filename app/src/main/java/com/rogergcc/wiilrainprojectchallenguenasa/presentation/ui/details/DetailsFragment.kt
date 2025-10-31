package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.weather.WeatherRepositoryAssets
import com.rogergcc.wiilrainprojectchallenguenasa.databinding.FragmentDashboardBinding
import com.rogergcc.wiilrainprojectchallenguenasa.databinding.FragmentDetailsBinding
import com.rogergcc.wiilrainprojectchallenguenasa.domain.WeatherFormatter
import com.rogergcc.wiilrainprojectchallenguenasa.domain.mapper.WeatherRecordMapper
import com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase.WeatherHistoricalReportUseCase
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.BUNDLE_LOCATION_SEARCH
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.LoadingView
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.model.LocationSearch
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.providers.AndroidResourceProvider
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null
    private val binding get() = _binding ?: error("Binding not initialized")

    private lateinit var loader: LoadingView

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
        }
    }

    private val weatherFormatter by lazy {
        WeatherFormatter(
            AndroidResourceProvider(requireContext())
        )
    }
    private val repositoryAssets by lazy {
        WeatherRepositoryAssets(
            requireContext(),
        )
    }
    private val weatherUseCase by lazy{
        WeatherHistoricalReportUseCase(
            weatherRepository = repositoryAssets,
            weatherFormatter = weatherFormatter,
            weatherRecordMapper = WeatherRecordMapper()
        )
    }
    private val viewModel by viewModels<WeatherDetailViewModel> {
        WeatherDetailViewModelFactory(
            weatherUseCase
        )
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        loader = LoadingView(requireContext())
//        val selectedType = arguments?.getString("selectedValue")
        val selectedLocation = arguments?.let {
            BundleCompat.getParcelable(it, BUNDLE_LOCATION_SEARCH, LocationSearch::class.java)
        }
        val weatherType = WeatherType.fromDescription(selectedLocation?.type ?: "-", requireContext())

        binding.title.text = selectedLocation?.selectedDateString + " - " + selectedLocation?.city + ", " + selectedLocation?.country
        binding.dateHistoric.text = selectedLocation?.historicEvaluation
//        binding.weatherTextView.text = viewModel.getWeatherText(weatherType)
        viewModel.loadWeatherReport(weatherType)

//        binding.weatherTextView.text = viewModel.getDetailsHistoricalRaw(weatherType)

        viewLifecycleOwner.lifecycleScope.launch {

            viewModel.weatherState.collect { state ->
                when (state) {
                    is WeatherDetailViewModel.UiState.Loading -> {
                        loader.show()
                    }
                    is WeatherDetailViewModel.UiState.Success -> {
                        loader.hide()
                        binding.weatherTextView.text = state.data
                    }
                    is WeatherDetailViewModel.UiState.Error -> {
                        loader.hide()
                        binding.weatherTextView.text = "Error: ${state.message}"
                    }
                }
            }
        }



    }


    companion object {

    }
}