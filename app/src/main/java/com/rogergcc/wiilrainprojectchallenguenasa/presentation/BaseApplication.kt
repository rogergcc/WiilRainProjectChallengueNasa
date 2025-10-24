package com.rogergcc.wiilrainprojectchallenguenasa.presentation;

import android.app.Application;
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.providers.ContextProvider

/**
 * Created on octubre.
 * year 2025 .
 */
class BaseApplication : Application() {
    override fun onCreate() {
        super.onCreate()
//        ContextProvider.initialize(this)
    }
}