package com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.providers

import android.content.Context
import androidx.annotation.StringRes


/**
 * Created on octubre.
 * year 2025 .
 */
class AndroidResourceProvider(private val context: Context) : ResourceProvider {
    override fun getString(@StringRes resId: Int): String = context.getString(resId)
    override fun getString(@StringRes resId: Int, vararg args: Any): String =
        context.getString(resId, *args)
}
