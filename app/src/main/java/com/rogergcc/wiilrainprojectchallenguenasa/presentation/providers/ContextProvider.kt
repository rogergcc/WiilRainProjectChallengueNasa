package com.rogergcc.wiilrainprojectchallenguenasa.presentation.providers

import android.content.Context


/**
 * Created on octubre.
 * year 2025 .
 */
object ContextProvider {
    private var appContext: Context? = null

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun getContext(): Context {
        return appContext ?: throw IllegalStateException("ContextProvider is not initialized.")
    }
}