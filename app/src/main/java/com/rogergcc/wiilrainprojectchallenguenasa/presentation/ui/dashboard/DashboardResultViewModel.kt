package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset
import com.rogergcc.wiilrainprojectchallenguenasa.domain.WeatherRepository
import com.rogergcc.wiilrainprojectchallenguenasa.domain.mapper.WeatherRecordMapper
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.ClimateAnalysisResult
import com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase.AnalyzeClimateUseCase
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TEST_LOG_TAG
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.model.DashboardUiMapper
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.model.DashboardUiState
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext


/**
 * Created on octubre.
 * year 2025 .
 */
class DashboardResultViewModel(
    private val weatherRepository: WeatherRepository,
    private val analyzeClimateUseCase: AnalyzeClimateUseCase,
) : ViewModel() {

    sealed class UiState {
        data object Loading : UiState()
        data class Success(
            val uiState: DashboardUiState
        ) : UiState()
        data class Failure(val errorMessage: UiText) : UiState()
    }

    private val _uiWeatherResultState = MutableStateFlow<UiState>(UiState.Loading)
    val uiWeatherResult: StateFlow<UiState> get() = _uiWeatherResultState

    fun calculateProbabilities() {
//        _uiWeatherResultState.value = UiState.Loading
        _uiWeatherResultState.update { UiState.Loading }

        viewModelScope.launch {
            runCatching {
                val dataset = withContext(Dispatchers.Default) {
                    weatherRepository.parseWeatherDataset()
                }

                // análisis (si es costoso, mantén en Default)
                val analysis = withContext(Dispatchers.Default) {
                    analyzeClimateUseCase.invoke(dataset.yearly_data)
                }

                DashboardUiMapper().map(dataset, analysis)
            }.onSuccess { mappedUi ->
                _uiWeatherResultState.update { UiState.Success(mappedUi) }
            }.onFailure { throwable ->
                throwable.printStackTrace()
                _uiWeatherResultState.update {
                    UiState.Failure(
                        UiText.StringResource(R.string.error_generic) // ajusta recurso
                    )
                }
            }
        }
    }

}

class DashboardResultViewModelFactory(
    private val weatherRepository: WeatherRepository,
    private val analyzeClimateUseCase: AnalyzeClimateUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboardResultViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboardResultViewModel(weatherRepository,
                analyzeClimateUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}