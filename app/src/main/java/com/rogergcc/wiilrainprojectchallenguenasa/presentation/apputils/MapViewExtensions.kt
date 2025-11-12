package com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils

import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.util.Log
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ConstrainMode
import com.mapbox.maps.GlyphsRasterizationMode
import com.mapbox.maps.GlyphsRasterizationOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.ResourceOptions
import com.mapbox.maps.Style
import com.mapbox.maps.TileStoreUsageMode
import com.mapbox.maps.applyDefaultParams
import com.mapbox.maps.extension.observable.subscribeMapLoadingError
import com.mapbox.maps.extension.observable.subscribeStyleLoaded
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.plugin.Plugin
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.rogergcc.wiilrainprojectchallenguenasa.BuildConfig
import com.rogergcc.wiilrainprojectchallenguenasa.R


/**
 * Created on noviembre.
 * year 2025 .
 */
private const val DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID"
private const val DROPPED_MARKER_SOURCE_ID = "dropped-marker-source-id"
private const val DROPPED_ICON_IMAGE_ID = "dropped-icon-image"
fun initDroppedMarkerLayer(
    style: Style,
    resources: Resources,
) {
    // Add the marker image to map
    val bitmap = BitmapFactory.decodeResource(resources, R.drawable.blue_marker)
    style.addImage(DROPPED_ICON_IMAGE_ID, bitmap)

    // Add the source
    style.addSource(geoJsonSource(DROPPED_MARKER_SOURCE_ID) {})

    // Add the layer
    style.addLayer(
        symbolLayer(DROPPED_MARKER_LAYER_ID, DROPPED_MARKER_SOURCE_ID) {
            iconImage(DROPPED_ICON_IMAGE_ID)
            iconAllowOverlap(true)
            iconIgnorePlacement(true)
            visibility(Visibility.NONE)
        }
    )

}

fun MapView.initDefault(context: Context) {
    val mapOptions = MapOptions.Builder()
        .applyDefaultParams(context)
        .constrainMode(ConstrainMode.HEIGHT_ONLY)
        .glyphsRasterizationOptions(
            GlyphsRasterizationOptions.Builder()
                .rasterizationMode(GlyphsRasterizationMode.IDEOGRAPHS_RASTERIZED_LOCALLY)
                // Font family is required when the GlyphsRasterizationMode is set to IDEOGRAPHS_RASTERIZED_LOCALLY or ALL_GLYPHS_RASTERIZED_LOCALLY
                .fontFamily("sans-serif")
                .build()
        )
        .crossSourceCollisions(false)
        .optimizeForTerrain(false)
        .build()

    val initialCameraOptions = CameraOptions.Builder()
        //-18.005089, -70.241262
        .center(Point.fromLngLat(-70.241262, -18.005089))
        .zoom(12.0)
//            .bearing(120.0)
        .build()
    val plugins = listOf(
        Plugin.Mapbox(Plugin.MAPBOX_ATTRIBUTION_PLUGIN_ID)
    )

    // set token and tile store usage mode for this particular map view, these settings will overwrite the default value.

    val resourceOptions = ResourceOptions.Builder()
        .accessToken(BuildConfig.MAPBOX_DEFAULT_TOKEN)
        .tileStoreUsageMode(TileStoreUsageMode.DISABLED)
        .applyDefaultParams(context)
        .build()

    // Find the container for the MapView
//    val mapContainer = view.findViewById<FrameLayout>(R.id.mapContainer)

    val mapInitOptions =
        MapInitOptions(context, resourceOptions, mapOptions, plugins, initialCameraOptions, true)
    // Create the MapView programmatically
    val mapView = MapView(context, mapInitOptions)
    // Add the MapView to the container
//    mapContainer.addView(mapView)

    getMapboxMap().subscribeMapLoadingError {
        // Error occurred when loading the map, try to handle it gracefully here
        Log.e(TEST_LOG_TAG, "subscribeMapLoadingError initDefault: ${it.data} ")
    }
    getMapboxMap().subscribeStyleLoaded {
        Log.d(TEST_LOG_TAG, "Style loaded successfully")
    }
    getMapboxMap().setCamera(initialCameraOptions)


//    getMapboxMap()
//        .loadStyleUri(
//        context.getString(R.string.mapbox_access_token_styled)
//    ) { style ->
//        // Map is set up and the style has loaded. Now you can add data or make other map adjustments.
//        scalebar.enabled = false
//        location.enabled = true
//        gestures.quickZoomEnabled = true
//
//        val hoveringMarker: ImageView = ImageView(context)
//        hoveringMarker.setImageResource(R.drawable.red_marker)
//
//        val params = FrameLayout.LayoutParams(
//            ViewGroup.LayoutParams.WRAP_CONTENT,
//            ViewGroup.LayoutParams.WRAP_CONTENT, Gravity.CENTER
//        )
//        hoveringMarker.layoutParams = params
//
//        addView(hoveringMarker)
//
//        // Initialize, but don't show, a SymbolLayer for the marker icon which will represent a selected location.
//        initDroppedMarkerLayer(style, context.resources)
//    }

//    getMapboxMap().loadStyle(
//        style(Style.DARK) {
//            +geoJsonSource("source-id") {
//                // Add GeoJSON data or other source properties here
//            }
//        }
//    )

    getMapboxMap().apply {
        loadStyleUri(Style.DARK) {
        }

        scalebar.enabled = false
        location.enabled = true
        gestures.quickZoomEnabled = true
    }

}

fun MapView.initDefaultXm(context: Context) {
    val initialCameraOptions = CameraOptions.Builder()
        //-18.005089, -70.241262
        .center(Point.fromLngLat(-70.241262, -18.005089))
        .zoom(12.0)
//            .bearing(120.0)
        .build()


    getMapboxMap().subscribeMapLoadingError {
        // Error occurred when loading the map, try to handle it gracefully here
        Log.e(TEST_LOG_TAG, "subscribeMapLoadingError initDefault: ${it.data} ")
    }
    getMapboxMap().subscribeStyleLoaded {
        Log.d(TEST_LOG_TAG, "Style loaded successfully")
    }
    getMapboxMap().setCamera(initialCameraOptions)


    getMapboxMap().apply {
        loadStyleUri(Style.DARK) {
        }

        scalebar.enabled = false
        location.enabled = true
        gestures.quickZoomEnabled = true
    }

}