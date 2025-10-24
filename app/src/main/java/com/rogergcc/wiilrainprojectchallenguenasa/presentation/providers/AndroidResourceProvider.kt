package com.rogergcc.wiilrainprojectchallenguenasa.presentation.providers

import android.content.Context


/**
 * Created on octubre.
 * year 2025 .
 */
class AndroidResourceProvider(private val context: Context) : ResourceProvider {
 override fun getString(resId: Int): String = context.getString(resId)
}