package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.home

import android.graphics.Bitmap
import android.graphics.BitmapFactory
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
import com.mapbox.geojson.Point
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.PolygonAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.createPolygonAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.data.weather.MapboxGeocodingRepository
import com.rogergcc.wiilrainprojectchallenguenasa.databinding.FragmentHomeBinding
import com.rogergcc.wiilrainprojectchallenguenasa.domain.usecase.GeoCodingUseCase
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.BUNDLE_LOCATION_SEARCH
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TEST_LOG_TAG
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.fullScreen
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.initDefault
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.initDefaultXm
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.restartDefaultStyles
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.setOnSingleClickListener
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.toast
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.model.LocationSearch
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.dashboard.MaterialDatePickerFragment
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Date


class HomeFragment : Fragment(R.layout.fragment_home) {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding ?: error("Binding not initialized")

    private var locationSearch: LocationSearch? = null
    private lateinit var pointAnnotationManager: PointAnnotationManager
    private lateinit var polygonAnnotationManager: PolygonAnnotationManager

    private var markerBitmap: Bitmap? = null // Caché para la imagen del marcador

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        lifecycleScope.launch {
            viewModel.homeState.collect { state ->
                when (state) {
                    is HomeViewModel.UiState.Loading -> {
                        Log.d(TEST_LOG_TAG, "[Home] Loading geocoding...")
                    }

                    is HomeViewModel.UiState.Success -> {
                        Log.d(TEST_LOG_TAG, "[Home] Geocoding success: ${state.data}")
//                        requireContext().toast("[Home] Location found: ${state.data}")

                        binding.apTvCitySearch.text = "📍 ${state.data.regionName}, ${state.data.countryName}"
                        // Ensure locationSearch is initialized
                        locationSearch = (locationSearch ?: LocationSearch()).copy(
                            city = state.data.regionName,
                            country = state.data.countryName
                        )
                    }

                    is HomeViewModel.UiState.Error -> {
                        Log.d(TEST_LOG_TAG, "[Home] Geocoding error: ${state.message}")
                    }
                }
            }
        }
    }

    private val geocodingRepository by lazy {
        MapboxGeocodingRepository(
            requireContext(),
        )
    }
    private val geoCodingUseCase by lazy {
        GeoCodingUseCase(
            geocodingRepository = geocodingRepository,
        )
    }
    private val viewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory(
            geoCodingUseCase
        )
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Reset the window settings when leaving the fragment
        requireActivity().restartDefaultStyles()
        markerBitmap?.recycle()
        markerBitmap = null

        locationSearch = null
        _binding = null
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        markerBitmap = BitmapFactory.decodeResource(resources, R.drawable.red_marker)
        return binding.root

    }

    private fun addMarker(point: Point) {
        pointAnnotationManager.deleteAll()
        markerBitmap?.let {
            val marker = PointAnnotationOptions()
                .withPoint(point)
                .withIconImage(it)
            pointAnnotationManager.create(marker)
        }
//        binding.tvCoordinates.text = "Lat: %.5f, Lon: %.5f".format(point.latitude(), point.longitude())
        Log.d(TEST_LOG_TAG,
            "[Home] Marker added at: Lat %.5f, Lon %.5f".format(point.latitude(), point.longitude())
        )
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
//        initMap()
        requireActivity().fullScreen()
        binding.mapView.initDefaultXm(requireContext())

        binding.mapView.gestures.addOnMapLongClickListener() { point ->
            addMarker(point)
            viewModel.getLocationDetails(point.latitude(), point.longitude())
            true
        }
        pointAnnotationManager = binding.mapView.annotations.createPointAnnotationManager()
        polygonAnnotationManager = binding.mapView.annotations.createPolygonAnnotationManager()

        binding.cardSearchJob.setOnSingleClickListener() {
//            showSearchBottomSheet()
        }

        binding.cardDatePickerSearch.setOnSingleClickListener() {
            MaterialDatePickerFragment(
                initialDate = Date(),
                tagName = "dateSearchWeather",
                onDateSelected = { year, month, day, tag ->
                    val formatted = "%02d/%02d/%04d".format(day + 1, month + 1, year)

                    binding.dateSelectedSearch.text = "📆 ${day + 1}/${month + 1}/$year"

                    val selectedDate = Calendar.getInstance().apply {
                        set(Calendar.YEAR, year)
                        set(Calendar.MONTH, month)
                        set(Calendar.DAY_OF_MONTH, day + 1)
                    }.time
                    // Ensure locationSearch is initialized
                    locationSearch = (locationSearch ?: LocationSearch()).copy(
                        date = Date(selectedDate.time),
                        selectedDateString = formatted
                    )
                    Log.d(TEST_LOG_TAG,
                        "[Home] Selected Date object: cal = ${locationSearch?.date}"
                    )
                }
            ).show(parentFragmentManager, "material_date_picker")

        }

        binding.btnResultClimate.setOnSingleClickListener {

            Log.d(TEST_LOG_TAG, "[Home] Search button clicked with date: $locationSearch")
            if (locationSearch?.date == null) {
                requireContext().toast(ContextCompat.getString(requireContext(), R.string.select_date_to_search))
                return@setOnSingleClickListener
            }

            if(locationSearch?.city.isNullOrEmpty()) {
                requireContext().toast(ContextCompat.getString(requireContext(), R.string.select_place_to_search))
                return@setOnSingleClickListener
            }

            Log.d(TEST_LOG_TAG,
                "[Home] Navigating to Dashboard location: $locationSearch"
            )
            val bundle = Bundle().apply {
                putParcelable(BUNDLE_LOCATION_SEARCH, locationSearch)
            }
            findNavController().navigate(R.id.gotoDashboard, bundle)

        }
    }

    private fun uiListeners() {
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.repeatOnLifecycle(Lifecycle.State.CREATED) {
                viewModel.homeState.collect { state ->
                    when (state) {
                        is HomeViewModel.UiState.Loading -> {
                            Log.d(TEST_LOG_TAG, "[Home] Loading geocoding...")
                        }

                        is HomeViewModel.UiState.Success -> {
                            Log.d(TEST_LOG_TAG, "[Home] Geocoding success: ${state.data}")
//                            requireContext().toast("[Home] Location found: ${state.data}")

                            binding.apTvCitySearch.text = "📍 ${state.data.regionName}, ${state.data.countryName}"
                            // Ensure locationSearch is initialized
                            locationSearch = (locationSearch ?: LocationSearch()).copy(
                                city = state.data.regionName,
                                country = state.data.countryName
                            )
                        }

                        is HomeViewModel.UiState.Error -> {
                            Log.d(TEST_LOG_TAG, "[Home] Geocoding error: ${state.message}")
                        }
                    }
                }
            }
        }
    }

    companion object {

    }
}