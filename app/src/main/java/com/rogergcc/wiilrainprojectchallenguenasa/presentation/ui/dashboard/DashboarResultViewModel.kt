package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.dashboard

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.data.dummy.ForecastResponse
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherDataset
import com.rogergcc.wiilrainprojectchallenguenasa.domain.WeatherRepository
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TEST_LOG_TAG
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.details.WeatherDetailViewModel
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.details.WeatherFormatter
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * Created on octubre.
 * year 2025 .
 */
class DashboarResultViewModel(
    private val weatherRepository: WeatherRepository,
) : ViewModel() {

    sealed class DetailUiState {
        data object Loading : DetailUiState()
        data class Success(
            val weatherDataset: WeatherDataset? = null,
        ) : DetailUiState()

        data class Failure(val errorMessage: UiText) : DetailUiState()
    }

    private val _uiWeatherResultState = MutableStateFlow<DetailUiState>(DetailUiState.Loading)
    val uiWeatherResult: StateFlow<DetailUiState> get() = _uiWeatherResultState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        showErrorState()
    }

    private fun showErrorState() {
        _uiWeatherResultState.value = DetailUiState.Failure(
            UiText.StringResource(
                R.string.error_message,
                listOf("Unknown error")
            )
        )
    }

    fun calculateProbabilities() {
        val weatherDatasetSample = weatherRepository.parseWeatherDataset()
        _uiWeatherResultState.value = DetailUiState.Loading
        viewModelScope.launch(Dispatchers.IO + coroutineExceptionHandler) {
            try {
                _uiWeatherResultState.value = DetailUiState.Success(
                    weatherDataset = weatherDatasetSample.copy()
                )
            } catch (e: Exception) {
                Log.e(TEST_LOG_TAG, "markFavoriteJobPosition: ${e.message}")
                showErrorState()
            }
        }

        // Usar los datos
    }
}

class DashboarResultViewModelFactory(
    private val weatherRepository: WeatherRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DashboarResultViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return DashboarResultViewModel(weatherRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}