package com.rogergcc.wiilrainprojectchallenguenasa.data.model.ranges


/**
 * Created on octubre.
 * year 2025 .
 */

interface Recommendation {
    val conditionRange: ClosedRange<Float>
    val color: Int
    val emoji: String
    val labelRes: Int
    val descRes: Int
    fun matches(value: Float): Boolean
}