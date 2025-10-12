package com.rogergcc.wiilrainprojectchallenguenasa.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.time.LocalDate
import java.time.LocalDateTime
import java.util.Date


/**
 * Created on octubre.
 * year 2025 .
 */
@Parcelize
data class LocationSearch(
    var city: String = "",
    var country: String = "",
    var selectedDate: Date,
    val selectedDateString: String = "",
    var location: SimpleLocation = SimpleLocation(0.0, 0.0),
) : Parcelable

@Parcelize
data class SimpleLocation(
    val latitude: Double,
    val longitude: Double,
) : Parcelable