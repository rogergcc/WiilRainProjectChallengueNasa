package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.PlaceInfo
import com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase.GeoCodingUseCase
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TEST_LOG_TAG
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch


/**
 * Created on noviembre.
 * year 2025 .
 */
class HomeViewModel(
    private val geoCodingUseCase: GeoCodingUseCase
) : ViewModel() {
    sealed class UiState<out T> {
        object Loading : UiState<Nothing>()
        data class Success<T>(val data: T) : UiState<T>()
        data class Error(val message: String) : UiState<Nothing>()
    }

    private val _homeState = MutableStateFlow<UiState<PlaceInfo>>(UiState.Loading)
    val homeState: StateFlow<UiState<PlaceInfo>> = _homeState.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UiState.Loading

    )
    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
        showErrorState()
    }
    private fun showErrorState() {
        _homeState.value = UiState.Error(
            "An error occurred while loading data."
        )
    }

    fun getLocationDetails(latitude: Double, longitude: Double) {
        _homeState.value = UiState.Loading
        viewModelScope.launch(Dispatchers.Default + coroutineExceptionHandler) {
            delay(500)
            runCatching {
                geoCodingUseCase.placeInfoFromCoordinates(latitude, longitude
                )
            }.onSuccess { formatted ->
                _homeState.value = HomeViewModel.UiState.Success(formatted)
            }.onFailure { e ->
                Log.e(TEST_LOG_TAG, "[Home] Error loading report: ${e.message}", e)
                showErrorState()
            }
        }
    }
}

class HomeViewModelFactory(
    private val geoCodingUseCase: GeoCodingUseCase
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return HomeViewModel(geoCodingUseCase) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}