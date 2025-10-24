package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.dashboard

sealed class UiText {
    data class DynamicString(val value: String) : UiText()
    data class StringResource(val resId: Int, val args: List<Any> = emptyList()) : UiText()

}
