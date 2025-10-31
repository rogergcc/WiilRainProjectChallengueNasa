package com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.View
import com.rogergcc.wiilrainprojectchallenguenasa.R


class LoadingView(context: Context) : Dialog(context,R.style.Theme_WiilRainProjectChallengueNasa_FullScreen) {
    init {
        this.setContentView(R.layout.view_loading)
        window!!.decorView.systemUiVisibility =
            (View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY or View.SYSTEM_UI_FLAG_HIDE_NAVIGATION or View.SYSTEM_UI_FLAG_FULLSCREEN)
        window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        this.setCancelable(false)
    }
}