package com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.providers


/**
 * Created on octubre.
 * year 2025 .
 */
interface ResourceProvider {
    fun getString(resId: Int, vararg args: Any): String
    fun getString(resId: Int): String


}