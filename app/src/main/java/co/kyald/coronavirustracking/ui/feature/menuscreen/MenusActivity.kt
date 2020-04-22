package co.kyald.coronavirustracking.ui.feature.menuscreen

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.*
import co.kyald.coronavirustracking.R
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.activity_menu.*


class MenusActivity : AppCompatActivity() {


    lateinit var navController: NavController
    lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)

        val navView: BottomNavigationView = findViewById(R.id.nav_view)

        navController = findNavController(R.id.nav_host_fragment)
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        appBarConfiguration = AppBarConfiguration(
            setOf(
                R.id.navigation_graph,  R.id.navigation_trending,R.id.navigation_notifications
            )
        )

        navView.setupWithNavController(navController)

    }
    override fun onSupportNavigateUp(): Boolean {
        val result = Intent()
        result.putExtra("refresh", true)
        setResult(Activity.RESULT_OK, result)
        finish()
        return true
    }

    override fun onNavigateUp(): Boolean {
        val result = Intent()
        result.putExtra("refresh", true)
        setResult(Activity.RESULT_OK, result)
        finish()
        return true
    }

    override fun onBackPressed() {
        val result = Intent()
        result.putExtra("refresh", true)
        setResult(Activity.RESULT_OK, result)
        finish()
    }

}
