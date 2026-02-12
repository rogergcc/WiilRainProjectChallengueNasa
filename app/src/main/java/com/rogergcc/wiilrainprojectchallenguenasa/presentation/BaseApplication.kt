package com.rogergcc.wiilrainprojectchallenguenasa.presentation;

import android.app.Application;
import com.mapbox.maps.ResourceOptions
import com.mapbox.maps.ResourceOptionsManager
import com.mapbox.maps.applyDefaultParams
import com.rogergcc.wiilrainprojectchallenguenasa.BuildConfig

/**
 * Created on octubre.
 * year 2025 .
 */
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        ResourceOptionsManager.getDefault(this, BuildConfig.MAPBOX_ACCESS_TOKEN) // FOR RELEASE


    }
}