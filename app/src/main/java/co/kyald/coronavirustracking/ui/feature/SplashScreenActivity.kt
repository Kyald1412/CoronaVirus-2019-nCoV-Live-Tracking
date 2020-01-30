package co.kyald.coronavirustracking.ui.feature

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.ui.feature.launchscreen.MainActivity
import org.jetbrains.anko.startActivity

class SplashScreenActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            startActivity<MainActivity>()
            finish()
        }, 1500)
    }
}
