package com.rogergcc.wiilrainprojectchallenguenasa.presentation.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.Date


/**
 * Created on octubre.
 * year 2025 .
 */
@Parcelize
data class LocationSearch(
    var type: String = "",
    var city: String = "",
    var country: String = "",
    var date: Date = Date(),
    var selectedDateString: String = "",
    var historicEvaluation: String = "",
    var location: SimpleLocation = SimpleLocation(0.0, 0.0),
) : Parcelable

@Parcelize
data class SimpleLocation(
    val latitude: Double,
    val longitude: Double,
) : Parcelable