package com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.splash

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.rogergcc.wiilrainprojectchallenguenasa.R
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.ui.MainActivity
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.adjustToolbarMarginForNotch
import com.rogergcc.wiilrainprojectchallenguenasa.presentation.apputils.hideSystemUIAndNavigation
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

class SplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.SplashThemeCorrect)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        CoroutineScope(Dispatchers.Main).launch {
            delay(SPLASH_TIME_OUT.toLong())

            Intent(this@SplashActivity, MainActivity::class.java).apply {
                startActivity(this)
                overridePendingTransition(android.R.anim.fade_in,android.R.anim.fade_out)
            }
            finish()
        }
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            hideSystemUIAndNavigation()
            adjustToolbarMarginForNotch()
        }
    }
    companion object {
        private const val SPLASH_TIME_OUT = 2500
    }
}