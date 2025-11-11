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
    private val RADIUS_WHEN_CIRCLES_MATCH_ICON_RADIUS = 14.0
    private val RADIUS_WHEN_CIRCLES_MATCH_ICON_RADIUS_D:Double = 14.0
    private val ZOOM_LEVEL_FOR_START_OF_BASE_CIRCLE_EXPANSION = 11.0
    private val ZOOM_LEVEL_FOR_SWITCH_FROM_CIRCLE_TO_ICON = 12.0
    private val FINAL_OPACITY_OF_SHADING_CIRCLE = .5
    private val BASE_CIRCLE_COLOR = "#3BC802"
    private val SHADING_CIRCLE_COLOR = "#858585"
    private val BASE_CIRCLE_LAYER_ID = "BASE_CIRCLE_LAYER_ID"
    private const val MARKER_SOURCE = "markers-source"
    private val BASE_CIRCLE_INITIAL_RADIUS = 3.4
    private const val MARKER_STYLE_LAYER = "markers-style-layer"
    private const val MARKER_IMAGE = "custom-marker"
    private const val SHADOW_CIRCLE_LAYER_ID = "SHADOW_CIRCLE_LAYER_ID"

    private const val DROPPED_MARKER_LAYER_ID = "DROPPED_MARKER_LAYER_ID"

    private const val STORE_ICON_SOURCE_ID = "store-location-source-id"
    fun circleLayer(style: Style) {
        val baseCircleLayer = CircleLayer(BASE_CIRCLE_LAYER_ID, MARKER_SOURCE).apply {
            circleColor(Color.parseColor(BASE_CIRCLE_COLOR))
            circleRadius(
                Expression.interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(ZOOM_LEVEL_FOR_START_OF_BASE_CIRCLE_EXPANSION)
                        literal(BASE_CIRCLE_INITIAL_RADIUS)
                    }
                    stop {
                        literal(ZOOM_LEVEL_FOR_SWITCH_FROM_CIRCLE_TO_ICON)
                        literal(RADIUS_WHEN_CIRCLES_MATCH_ICON_RADIUS)
                    }
                }
            )

        }
        style.addLayer(baseCircleLayer)
    }
    fun addLayerBelow(style: Style) {
        val shadowTransitionCircleLayer = CircleLayer(SHADOW_CIRCLE_LAYER_ID, MARKER_SOURCE).apply {
            circleColor(Color.parseColor(SHADING_CIRCLE_COLOR))
            circleRadius(
                RADIUS_WHEN_CIRCLES_MATCH_ICON_RADIUS_D
            )
            circleOpacity(
                Expression.interpolate {
                    linear()
                    zoom()
                    stop {
                        literal(ZOOM_LEVEL_FOR_START_OF_BASE_CIRCLE_EXPANSION - .5)
                        literal(0.0)
                    }
                    stop {
                        literal(ZOOM_LEVEL_FOR_START_OF_BASE_CIRCLE_EXPANSION)
                        literal(FINAL_OPACITY_OF_SHADING_CIRCLE)

                    }
                }
            )
        }
        style.addLayerBelow(shadowTransitionCircleLayer, BASE_CIRCLE_LAYER_ID)

    }
    fun setUpUiSettings(mapView: MapView) {


        mapView.gestures.quickZoomEnabled = true
        mapView.gestures.scrollEnabled = true
        mapView.gestures.pitchEnabled = true

//        mapboxMap.getUiSettings().setZoomGesturesEnabled(true)
//        mapboxMap.getUiSettings().setAllGesturesEnabled(true)

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

        val pointAnnotationManager = mapView.annotations.createPointAnnotationManager().apply {
            // listen for click event
            addClickListener(
                OnPointAnnotationClickListener {
                    // define camera position
                    val cameraPosition = initialCameraOptions
                    // set camera position
                    mapView.getMapboxMap().setCamera(cameraPosition)
                    true
                }
            )
        }

        mapView.getMapboxMap().setCamera(initialCameraOptions)
    }

    fun setUpCameraAnimationPosition(mapView: MapView, mLatitude: Double, mLongitude: Double ) {

        val initialCameraOptions = CameraOptions.Builder()
            .center(Point.fromLngLat(mLongitude, mLatitude))
            .zoom(17.5)
//            .bearing(180.0)
            .build()

        val cameraAnimationsPlugin = mapView.camera
        cameraAnimationsPlugin.flyTo(
            initialCameraOptions,
            MapAnimationOptions.mapAnimationOptions {
                duration(3000L) // duration in ms
            }
        )

        mapView.getMapboxMap().setCamera(initialCameraOptions)
    }

    fun setUpCameraPositionMapbox(mMapView: MapView, mLatitude: Double, mLongitude: Double) {
//        val getCenterPosition = mapView.getMapboxMap().cameraState.center

        val style= mMapView.getMapboxMap().getStyle()

//        // Show the SymbolLayer icon to represent the selected map location
//        val source = style?.getSource("dropped-marker-source-id") as? GeoJsonSource
//        source?.feature(Point.fromLngLat(getCenterPosition.longitude(), getCenterPosition.latitude))
//
//        val droppedMarkerLayer = style?.getLayer("dropped-marker-source-id") as? SymbolLayer
//        droppedMarkerLayer?.visibility(Visibility.VISIBLE)

//        if( style?.getLayer(DROPPED_MARKER_LAYER_ID)!=null){
//
//        }

        // Show the SymbolLayer icon to represent the selected map location

        val droppedMarkerLayer = style?.getLayer(DROPPED_MARKER_LAYER_ID) as? SymbolLayer
        if (droppedMarkerLayer != null) {
            val source = style.getSource("dropped-marker-source-id") as? GeoJsonSource

            val point = Point.fromLngLat(mLongitude, mLatitude)
            val feature = Feature.fromGeometry(point)
            source?.feature(feature)

            droppedMarkerLayer.visibility(Visibility.VISIBLE)
        }
//        else {
//            // Add the layer if it does not exist
//            initDroppedMarkerLayer(style, mMapView.context.resources)
//            setUpCameraPositionMapbox(mMapView, mLatitude, mLongitude) // Retry setting up the camera position
//        }

    }

    fun hideSelectionLayer(mapView: MapboxMap) {
        // Hide the selected location SymbolLayer
        val droppedMarkerLayer = mapView.getStyle()?.getLayer(DROPPED_MARKER_LAYER_ID)

        droppedMarkerLayer?.visibility(Visibility.NONE)

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
        val styleUrl = context.getString(R.string.mapbox_style_url)

        mapboxMap.apply {             // Configuraciones opcionales de UI
            mapView.scalebar.enabled = false
            mapView.location.enabled = true
            mapView.gestures.quickZoomEnabled = true

            Log.d(TEST_LOG_TAG, "initMapDefault: Mapbox MapView initialized with custom style.")

        }

        return mapView
    }


}