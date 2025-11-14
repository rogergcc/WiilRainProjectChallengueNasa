package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.details

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rogergcc.wiilrainprojectchallenguenasa.data.model.WeatherType
import com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase.WeatherHistoricalReportUseCase
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TEST_LOG_TAG
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch


/**
 * Created on octubre.
 * year 2025 .
 */
class WeatherDetailViewModel(
    private val historicalDataUseCase: WeatherHistoricalReportUseCase,

    ) : ViewModel() {

    sealed class UiState<out T> {
        object Loading : UiState<Nothing>()
        data class Success<T>(val data: T) : UiState<T>()
        data class Error(val message: String) : UiState<Nothing>()
    }
    private val _weatherState = MutableStateFlow<UiState<String>>(UiState.Loading)
    val weatherState: StateFlow<UiState<String>> = _weatherState

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        showErrorState()
    }
    private fun showErrorState() {
        _weatherState.value = UiState.Error(
            "An error occurred while loading data."
        )
    }
    fun loadWeatherReport(weatherType: WeatherType) {
        _weatherState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.Default + coroutineExceptionHandler) {
            delay(2000)
            runCatching {
                historicalDataUseCase.invoke(weatherType)
            }.onSuccess { formatted ->
                _weatherState.value = UiState.Success(formatted)
            }.onFailure { e ->
                Log.e(TEST_LOG_TAG, "[Detail] Error loading loadWeatherReport: ${e.message}")
                showErrorState()
            }
        }
    }

}

class WeatherDetailViewModelFactory(
    private val historicalDataUseCase: WeatherHistoricalReportUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(WeatherDetailViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return WeatherDetailViewModel(historicalDataUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}