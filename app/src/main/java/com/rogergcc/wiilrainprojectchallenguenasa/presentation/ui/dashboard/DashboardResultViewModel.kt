package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset
import com.rogergcc.wiilrainprojectchallenguenasa.domain.WeatherRepository
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.ClimateAnalysisResult
import com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase.AnalyzeClimateUseCase
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TEST_LOG_TAG
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


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
            val weatherDataset: WeatherDataset,
            val analysis: ClimateAnalysisResult
        ) : UiState()
        data class Failure(val errorMessage: UiText) : UiState()
    }


    private val _uiWeatherResultState = MutableStateFlow<UiState>(UiState.Loading)
    val uiWeatherResult: StateFlow<UiState> get() = _uiWeatherResultState

    private val exceptionHandler  = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        showErrorState()
    }

    private fun showErrorState() {
        _uiWeatherResultState.value = UiState.Failure(
            UiText.StringResource(
                R.string.error_message
            )
        )
    }

    fun calculateProbabilities() {
        _uiWeatherResultState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.Default + exceptionHandler) {
            try {
                val dataset = weatherRepository.parseWeatherDataset()

                val analysis = analyzeClimateUseCase.invoke(dataset.yearly_data)
                _uiWeatherResultState.value = UiState.Success(dataset, analysis)
            } catch (e: Exception) {
                Log.e(TEST_LOG_TAG, "markFavoriteJobPosition: ${e.message}")
                showErrorState()
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