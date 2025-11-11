package com.rogergcc.wiilrainprojectchallenguenasa.data.weather

import android.content.Context
import android.util.Log
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Point
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.GeocodingResult
import com.rogergcc.wiilrainprojectchallenguenasa.domain.model.PlaceInfo
import com.rogergcc.wiilrainprojectchallenguenasa.domain.repository.GeocodingRepository
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TEST_LOG_TAG
import kotlinx.coroutines.suspendCancellableCoroutine
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

/**
 * Created on noviembre.
 * year 2025 .
 */
class MapboxGeocodingRepository(
    private val context: Context,
) : GeocodingRepository {
    override suspend fun reverseGeocode(point: Point): GeocodingResult {
        return suspendCancellableCoroutine { cont ->
        }
    }

    override suspend fun reverseGeocodingPlace(lat: Double, lon: Double): PlaceInfo {
        return suspendCancellableCoroutine { cont ->
            val geocoding = MapboxGeocoding.builder()
                .accessToken(context.getString(R.string.mapbox_access_token_styled))
                .query(Point.fromLngLat(lon, lat))
                .geocodingTypes(GeocodingCriteria.TYPE_PLACE)
                .build()

            geocoding.enqueueCall(object : Callback<GeocodingResponse> {
                override fun onResponse(
                    call: Call<GeocodingResponse>,
                    response: Response<GeocodingResponse>,
                ) {

                    if (!response.isSuccessful) {
                        cont.resumeWithException(Exception("Geocoding failed: ${response.code()}"))
                        return
                    }

                    val features = response.body()?.features()
                    if (features.isNullOrEmpty()) {
                        cont.resumeWithException(Exception("No features found"))
                        return
                    }
                    val feature = features.firstOrNull()

                    val region = feature?.context()?.get(0)
                    val country = feature?.context()?.get(1)
                    val featureName = features.firstOrNull()?.text() ?: "Unknown"

                    Log.d(TEST_LOG_TAG, "[GeocodingDebug] Full feature: $feature")

                    Log.d(
                        TEST_LOG_TAG,
                        "[GeocodingDebug] onResponse: \n region: ${region?.text()} \n" +
                                " country: ${country?.text()} \n" +
                                " featureName ${featureName} "
                    )
                    cont.resume(
                        PlaceInfo(
                            featureName = featureName,
                            regionName = region?.text() ?: "Unknown region",
                            countryName = country?.text() ?: "Unknown country"
                        )
                    )

                }

                override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                    if (cont.isActive) cont.resumeWithException(t)
                }
            })
            cont.invokeOnCancellation {
                try {
                    geocoding.cancelCall() // ← Este es el correcto
                } catch (e: Exception) {
                    // Ignorar error de cancelación
                    Log.e(TEST_LOG_TAG, "Error ${e.message}")
                }
            }
        }
    }


}