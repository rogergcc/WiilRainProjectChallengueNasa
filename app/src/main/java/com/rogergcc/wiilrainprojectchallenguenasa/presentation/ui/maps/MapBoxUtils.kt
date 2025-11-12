import android.content.ContentValues.TAG
import android.content.Context
import android.content.res.Resources
import android.graphics.BitmapFactory
import android.graphics.Color
import android.util.Log
import android.view.ViewGroup
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.ConstrainMode
import com.mapbox.maps.GlyphsRasterizationMode
import com.mapbox.maps.GlyphsRasterizationOptions
import com.mapbox.maps.MapInitOptions
import com.mapbox.maps.MapOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.MapboxMap
import com.mapbox.maps.ResourceOptions
import com.mapbox.maps.Style
import com.mapbox.maps.TileStoreUsageMode
import com.mapbox.maps.applyDefaultParams
import com.mapbox.maps.extension.style.expressions.generated.Expression
import com.mapbox.maps.extension.style.layers.addLayer
import com.mapbox.maps.extension.style.layers.addLayerBelow
import com.mapbox.maps.extension.style.layers.generated.CircleLayer
import com.mapbox.maps.extension.style.layers.generated.SymbolLayer
import com.mapbox.maps.extension.style.layers.generated.symbolLayer
import com.mapbox.maps.extension.style.layers.getLayer
import com.mapbox.maps.extension.style.layers.properties.generated.Visibility
import com.mapbox.maps.extension.style.sources.addSource
import com.mapbox.maps.extension.style.sources.generated.GeoJsonSource
import com.mapbox.maps.extension.style.sources.generated.geoJsonSource
import com.mapbox.maps.extension.style.sources.getSource
import com.mapbox.maps.plugin.Plugin
import com.mapbox.maps.plugin.animation.MapAnimationOptions
import com.mapbox.maps.plugin.animation.camera
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.OnPointAnnotationClickListener
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager
import com.mapbox.maps.plugin.gestures.gestures
import com.mapbox.maps.plugin.locationcomponent.location
import com.mapbox.maps.plugin.scalebar.scalebar
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.TEST_LOG_TAG

object MapBoxUtils {
    private val ZOOM_LEVEL_FOR_SWITCH_FROM_CIRCLE_TO_ICON = 12.0
    private const val MARKER_SOURCE = "markers-source"
    private const val MARKER_STYLE_LAYER = "markers-style-layer"
    private const val MARKER_IMAGE = "custom-marker"
    private const val SHADOW_CIRCLE_LAYER_ID = "SHADOW_CIRCLE_LAYER_ID"

    private const val DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID"

    private const val STORE_ICON_SOURCE_ID = "store-location-source-id"

    fun setUpUiSettings(mapView: MapView) {
        mapView.gestures.quickZoomEnabled = true
        mapView.gestures.scrollEnabled = true
        mapView.gestures.pitchEnabled = true

        mapView.gestures.updateSettings {
            scrollEnabled = true
            pinchToZoomEnabled = true

        }
        mapView.gestures.getSettings().pinchToZoomEnabled = true

    }

    fun addSymbolLayer(style: Style) {
        val symbolIconLayer = SymbolLayer(MARKER_STYLE_LAYER, MARKER_SOURCE).apply {
            iconImage(MARKER_IMAGE)
            iconIgnorePlacement(true)
            iconAllowOverlap(true)
            minZoom(ZOOM_LEVEL_FOR_SWITCH_FROM_CIRCLE_TO_ICON.toDouble())
        }

        style.addLayer(
            symbolIconLayer
        )

    }
    fun addSourceFeatureCollection(mapView: MapView, features: FeatureCollection) {
        val style = mapView.getMapboxMap().getStyle()
        val sourceId = STORE_ICON_SOURCE_ID
        val existingSource = style?.getSource(sourceId) as? GeoJsonSource

        if (existingSource != null) {
            existingSource.featureCollection(features)
        } else {
            style?.addSource(
                geoJsonSource(sourceId) {
                    featureCollection(features)
                }
            )
        }

//        style{
//            +geoJsonSource( STORE_ICON_SOURCE_ID) {
//                featureCollection(features)
//            }
//        }


    }

    fun setUpCamera(mLatitude: Double, mLongitude: Double, mapView: MapView) {
        val initialCameraOptions = CameraOptions.Builder()
            .center(Point.fromLngLat(mLongitude, mLatitude))
            .pitch(45.0)
            .zoom(15.5)
            .bearing(-4.6)
            .build()

        mapView.getMapboxMap().setCamera(initialCameraOptions)
    }


    fun initMapDefault(context: Context, parent: ViewGroup): MapView {

        // 1️⃣ Configurar opciones avanzadas del mapa
        val mapOptions = MapOptions.Builder()
            .applyDefaultParams(context)
            .constrainMode(ConstrainMode.HEIGHT_ONLY)
            .glyphsRasterizationOptions(
                GlyphsRasterizationOptions.Builder()
                    .rasterizationMode(GlyphsRasterizationMode.IDEOGRAPHS_RASTERIZED_LOCALLY)
                    .fontFamily("sans-serif")
                    .build()
            )
            .crossSourceCollisions(false)
            .optimizeForTerrain(false)
            .build()

        val initialCameraOptions = CameraOptions.Builder()
            .center(Point.fromLngLat(-70.241262, -18.005089))
            .zoom(12.0)
            .build()

        // 3️⃣ Configurar ResourceOptions con token
        val accessToken = context.getString(R.string.mapbox_access_token_styled)
        val resourceOptions = ResourceOptions.Builder()
            .accessToken(accessToken)
            // READ_ONLY es más seguro que DISABLED (permite descargas online)
            .tileStoreUsageMode(TileStoreUsageMode.READ_ONLY)
            .applyDefaultParams(context)
            .build()

        // 4️⃣ Crear MapInitOptions aplicando todas las configuraciones
        val mapInitOptions = MapInitOptions(
            context = context,
            resourceOptions = resourceOptions,
            mapOptions = mapOptions,
            cameraOptions = initialCameraOptions,
            plugins = listOf(
                Plugin.Mapbox(Plugin.MAPBOX_ATTRIBUTION_PLUGIN_ID)
            ),
            textureView = true
        )

        // 5️⃣ Crear MapView programáticamente (para aplicar MapInitOptions)
        val mapView = MapView(context, mapInitOptions)
        parent.addView(mapView)

        // 6️⃣ Configurar y cargar tu estilo personalizado
        val mapboxMap = mapView.getMapboxMap()

        mapboxMap.apply {
            mapView.scalebar.enabled = false
            mapView.location.enabled = true
            mapView.gestures.quickZoomEnabled = true

            Log.d(TEST_LOG_TAG, "initMapDefault: Mapbox MapView initialized with custom style.")

        }

        return mapView
    }


}