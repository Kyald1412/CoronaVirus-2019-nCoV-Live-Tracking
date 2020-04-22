package co.kyald.coronavirustracking.ui.feature.mainscreen

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup.LayoutParams.WRAP_CONTENT
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.FrameLayout
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.data.database.model.CoronaEntity
import co.kyald.coronavirustracking.ui.adapter.CoronaRecyclerViewAdapter
import co.kyald.coronavirustracking.ui.feature.menuscreen.MenuActivity
import co.kyald.coronavirustracking.ui.feature.menuscreen.MenusActivity
import co.kyald.coronavirustracking.utils.Constants
import co.kyald.coronavirustracking.utils.Utils
import co.kyald.coronavirustracking.utils.extensions.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import com.mapbox.mapboxsdk.style.layers.TransitionOptions
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.marker_view.view.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*


class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel()
    private val preferences: SharedPreferences by inject()

    companion object {
        private lateinit var mapboxMap: MapboxMap
        private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>
        private lateinit var dataAdapter: CoronaRecyclerViewAdapter
        private lateinit var markerViewManager: MarkerViewManager
        private var marker: MutableList<MarkerView> = mutableListOf()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, Constants.MAPBOX_TOKEN)

        setContentView(R.layout.activity_main)

        setupRvData()

        initMap(savedInstanceState)
        initListener()
        initObserver()
        initView()

    }

    private fun initView() {
        btnRefresh.startAnimation(Utils().rotatingAnimation())
        sheetBehavior = BottomSheetBehavior.from(bottom_sheet)
    }

    private fun initObserver() {
        observe(viewModel.isFinishedLiveData, ::refreshLastUpdate)
        observe(viewModel.coronaLiveData, ::setupData)
        observe(viewModel.totalCases, ::updateText)
        observe(viewModel.currentDataSource, ::changeDataSource)

    }

    private fun changeDataSource(source: String) {
        tv_pull.text = "${getString(R.string.data_provided)} ${
        when (source) {
            Constants.DATA_SOURCE.DATA_S1.value -> "Channel New Asia"
            Constants.DATA_SOURCE.DATA_S2.value -> "Johns Hopkins CSSE"
            Constants.DATA_SOURCE.DATA_S3.value -> "ArcGIS-NCOV"
            Constants.DATA_SOURCE.DATA_S4.value -> "Worldometers"
            else -> "Channel New Asia"
        }
        }"
    }

    private fun updateText(data: Map<String, String>) {
        tv_infected.text = data.getValue("confirmed")
        tv_death.text = data.getValue("deaths")
        tv_recovered.text = data.getValue("recovered")
    }

    private fun setupData(data: List<CoronaEntity>) {
        dataAdapter.setEntity(data)
        setupMarker(data)
    }

    private fun refreshLastUpdate(data: Map<String, Boolean>) {

        if (data["done"] == true) {
            if (data["internet"] == false) {
                Toast.makeText(
                    this,
                    getString(R.string.network_problem),
                    Toast.LENGTH_SHORT
                ).show()

                tvLastUpdate.text = preferences.getString(Constants.PREF_LAST_UPDATE, "")

            } else {

                preferences.edit().putString(
                    Constants.PREF_LAST_UPDATE,
                    "${getString(R.string.last_update)} ${Date(System.currentTimeMillis()).toSimpleString()}"
                ).apply()

                tvLastUpdate.text = preferences.getString(Constants.PREF_LAST_UPDATE, "")
            }

            btnRefresh.clearAnimation()

        }

    }

    private fun initListener() {

        btnRefresh.setSafeOnClickListener {

            btnRefresh.startAnimation(Utils().rotatingAnimation())

            if (viewModel.isFinishedLiveData.value?.get("done") == true
            ) {
                viewModel.refreshData()
            } else {
                Toast.makeText(
                    this,
                    getString(R.string.refresh_timeout),
                    Toast.LENGTH_SHORT
                ).show()

                btnRefresh.clearAnimation()
            }
        }

        edt_search.OnTextChangedListener { text ->
            dataAdapter.filter.filter(text)
        }

        fabButton.setOnClickListener {
            startActivityForResult(Intent(this, MenusActivity::class.java), 20)
        }

    }

    private fun initMap(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map ->
            mapboxMap = map
            map.setStyle(
                when (preferences.getString(Constants.PREF_THEME, "2")?.toInt()!!) {
                    AppCompatDelegate.MODE_NIGHT_YES -> Style.DARK
                    AppCompatDelegate.MODE_NIGHT_NO -> Style.LIGHT
                    else -> Style.DARK
                }
            ) { style ->
                style.transition = TransitionOptions(0, 0, false)
                mapboxMap.animateCamera(
                    Constants().defaultLocation
                )

                markerViewManager = MarkerViewManager(mapView, mapboxMap)

            }
        }
    }

    private fun setupMarker(data: List<CoronaEntity>) {
        btnRefresh.startAnimation(Utils().rotatingAnimation())

        marker.forEach {
            markerViewManager.removeMarker(it)
        }

        markerViewManager.onDestroy()

        data.forEach {
            val customView = createCustomAnimationView(it)
            val markerView = MarkerView(
                LatLng(it.info.latitude!!, it.info.longitude!!)
                , customView
            )

            marker.add(markerView)

            marker.let {
                markerViewManager.addMarker(markerView)
            }
        }

        btnRefresh.clearAnimation()

    }

    private fun createCustomAnimationView(it: CoronaEntity)
            : View {

        val customView = LayoutInflater.from(this).inflate(R.layout.marker_view, null)
        customView.layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)

        customView.country.text = it.info.country

        when (preferences.getString(
            Constants.PREF_DATA_SOURCE,
            Constants.DATA_SOURCE.DATA_S4.value
        )) {
            Constants.DATA_SOURCE.DATA_S1.value -> customView.active_case.gone()
            Constants.DATA_SOURCE.DATA_S2.value -> customView.active_case.gone()
            Constants.DATA_SOURCE.DATA_S3.value -> customView.active_case.gone()
            Constants.DATA_SOURCE.DATA_S4.value -> customView.active_case.visible()
        }
        customView.active_case.text = "${getString(R.string.info_active)} ${it.info.case_actives}"
        customView.confirm_case.text =
            "${getString(R.string.info_confirm)} ${it.info.case_confirms}"
        customView.death_case.text = "${getString(R.string.info_death)} ${it.info.case_deaths}"
        customView.recovered_case.text =
            "${getString(R.string.info_recovered)} ${it.info.case_recovered}"

        customView.imageview.setOnClickListener {

            customView.animation_layout.measure(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val targetHeight =
                if (customView.animation_layout.layoutParams.height == 0) customView.animation_layout.measuredHeight else 0
            val targetWidth =
                if (customView.animation_layout.layoutParams.width == 0) customView.animation_layout.measuredWidth else 0

            val anim = ValueAnimator.ofInt(customView.animation_layout.measuredHeight, targetHeight)

            anim.interpolator = AccelerateDecelerateInterpolator()

            anim.addUpdateListener { valueAnimator ->
                val layoutParams = customView.animation_layout.layoutParams
                layoutParams.width =
                    ((targetWidth * valueAnimator.animatedFraction).toInt())
                layoutParams.height =
                    ((targetHeight * valueAnimator.animatedFraction).toInt())
                customView.animation_layout.layoutParams = layoutParams
            }

            anim.OnAnimationListener {
                val layoutParams = customView.animation_layout.layoutParams
                layoutParams.height = WRAP_CONTENT
            }

            anim.duration = 700
            anim.start()
        }
        return customView
    }

    private fun setupRvData() {

        dataAdapter =
            CoronaRecyclerViewAdapter(
                this
            )

        rvData.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        rvData.adapter = dataAdapter
        rvData.adapter?.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        when (resultCode) {

            Activity.RESULT_OK -> {
                if (viewModel.isFinishedLiveData.value?.get("done") == true
                ) {
//                    GlobalScope.launch{
//                        delay(2000)
//                        btnRefresh.startAnimation(Utils().rotatingAnimation())
//                        viewModel.refreshData()
//                    }
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

}