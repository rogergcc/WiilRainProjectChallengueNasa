package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.details

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.os.BundleCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.WeatherDataManager
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.data.weather.WeatherRepositoryAssets
import com.rogergcc.wiilrainprojectchallenguenasa.databinding.FragmentDetailsBinding
import com.rogergcc.wiilrainprojectchallenguenasa.domain.WeatherFormatter
import com.rogergcc.wiilrainprojectchallenguenasa.domain.WeatherRepository
import com.rogergcc.wiilrainprojectchallenguenasa.domain.mapper.WeatherRecordMapper
import com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase.GetFormattedWeatherUseCase
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.LoadingView
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.model.LocationSearch
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.providers.AndroidResourceProvider
import kotlinx.coroutines.launch

class DetailsFragment : Fragment() {

    private var _binding: FragmentDetailsBinding? = null

    private val binding get() = _binding!!
//    private val viewModel: WeatherDetailViewModel by viewModels()

    private val weatherFormatter by lazy {
        WeatherFormatter(
            AndroidResourceProvider(requireContext())
        )
    }
    private val jobsCacheRepository by lazy {
        WeatherRepositoryAssets(requireContext())
    }
    private val weatherUseCase by lazy{
        GetFormattedWeatherUseCase(
            weatherRepository = jobsCacheRepository,
            weatherFormatter = weatherFormatter,
            weatherRecordMapper = WeatherRecordMapper()
        )
    }
    private val viewModel by viewModels<WeatherDetailViewModel> {
        WeatherDetailViewModelFactory(
            weatherUseCase
        )
    }
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

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentDetailsBinding.inflate(inflater, container, false)
        return binding.root

    }
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        val dataManager = WeatherDataManager(requireContext())
        loader = LoadingView(requireContext())
//        val selectedType = arguments?.getString("selectedValue")
        val selectedLocation = arguments?.let {
            BundleCompat.getParcelable(it, "selectedLocationSearch", LocationSearch::class.java)
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

//        val datasetDummy = dataManager.parseWeatherDataset()
//        val yearlyDataBase= datasetDummy.yearly_data



//        chartContainer.setupHistoricalBarChart(
//            requireContext(),
//            data = datasetDummy.yearly_data,
//            getValue = { it.temp_c },
//            getMaxScale = { list -> list.maxOf { it.temp_c } },
//            getThreshold = { 32.0 },
//            valueFormatter = { "%.1f".format(it) },
//            unit = "%"
//        )
//        chartContainer.setupHistoricalBarChart(
//            requireContext(),
//            data = datasetDummy.yearly_data,
//            getValue = { it.temp_c },
//            getMaxScale = { data -> data.maxOf { it.temp_c } }, // Escala: min→max histórico
//            getThreshold = { 32.0 }, // Umbral calor extremo
////            getIcon = { temp -> if (temp > 32.0) Icons.Default.Warning else null },
//            valueFormatter = { "%.1f".format(it) },
//            unit = "°C"
//        )

//        val weatherForDate = dataAqpSearchDummy.variables?.find { it?.id == dateSearch }
//
//        showData(weatherForDate!!)

    }

//    private fun showData(detail: ForecastData.Variable) {
//        // Configurar gráfico
//        val entries = detail.chartData?.mapIndexed { index, mv ->
//            mv?.value?.let { BarEntry(index.toFloat(), it.toFloat()) }
//        }
//        val dataSet = BarDataSet(entries, detail.name).apply {
//            valueTextSize = 12f
//        }
//        val data = BarData(dataSet)
//        binding.barChart.data = data
//        binding.barChart.xAxis.valueFormatter = IndexAxisValueFormatter(detail.chartData?.map { it?.month })
//        binding.barChart.invalidate()
//
//
//        // Mostrar textos
//        binding. tvInterpretation.text = detail.historicalAverage?.interpretation
//        binding.tvAverage.text = "Media histórica: ${detail.historicalAverage?.averageRainfallMm} ${detail.unit}"
////        binding.tvRecommendation.text = detail.historicalAverage.summary.recommendation
//    }

    companion object {

    }
}