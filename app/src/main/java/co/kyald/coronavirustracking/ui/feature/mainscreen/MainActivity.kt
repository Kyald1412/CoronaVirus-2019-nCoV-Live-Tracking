package co.kyald.coronavirustracking.ui.feature.mainscreen

import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.widget.SearchView
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.data.database.model.chnasia.S1CoronaEntity
import co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.s1adapter.S1RecyclerViewAdapter
import co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.s2adapter.S2RecyclerViewAdapter
import co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.s3adapter.S3RecyclerViewAdapter
import co.kyald.coronavirustracking.ui.feature.preferencescreen.PreferenceActivity
import co.kyald.coronavirustracking.utils.Constants
import co.kyald.coronavirustracking.utils.Utils
import co.kyald.coronavirustracking.utils.extensions.gone
import co.kyald.coronavirustracking.utils.extensions.setSafeOnClickListener
import co.kyald.coronavirustracking.utils.extensions.toSimpleString
import co.kyald.coronavirustracking.utils.extensions.visible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.get
import com.mapbox.mapboxsdk.style.expressions.Expression.toString
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.layers.TransitionOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.android.synthetic.main.activity_main.*
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.net.URISyntaxException
import java.util.*


class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel()

    private val preferences: SharedPreferences by inject()

    private var mapboxMap: MapboxMap? = null

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    private var adapterS1: S1RecyclerViewAdapter? = null
    private var adapterS2: S2RecyclerViewAdapter? = null
    private var adapterS3: S3RecyclerViewAdapter? = null

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, Constants.MAPBOX_TOKEN)

        setContentView(R.layout.activity_main)

        setupRvS1()
        setupRvS2()
        setupRvS3()

        initMap(savedInstanceState)
        initListener()
        initObserver()
        initBottomSheet()
        initView()

    }

    override fun onRestart() {
        super.onRestart()
//        if (AppCompatDelegate.getDefaultNightMode() != mLastDayNightMode) {
//            recreate();
//        }
    }


    private fun initView() {

        btnRefresh.startAnimation(Utils().rotatingAnimation())

        when(preferences.getString(
            Constants.PREF_DATA_SOURCE,
            ""
        )){
            Constants.DATA_SOURCE.DATA_S1.value -> {
                llRecovered.gone()
                tvRecoverCase.gone()
            }

            Constants.DATA_SOURCE.DATA_S2.value -> {
                llRecovered.visible()
                tvRecoverCase.visible()
            }

            Constants.DATA_SOURCE.DATA_S3.value -> {
                llRecovered.visible()
                tvRecoverCase.visible()
            }
        }
    }

    private fun initBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from<LinearLayout>(bottom_sheet)

        /**
         * bottom sheet state change listener
         * we are changing button text when sheet changed state
         * */
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> tv_pull.text = "${getString(R.string.data_provided)} ${
                    
                    when(preferences.getString(
                        Constants.PREF_DATA_SOURCE,
                        Constants.DATA_SOURCE.DATA_S2.value
                    )){
                        Constants.DATA_SOURCE.DATA_S1.value -> "Channel New Asia"
                        Constants.DATA_SOURCE.DATA_S2.value -> "Johns Hopkins CSSE"
                        Constants.DATA_SOURCE.DATA_S3.value -> "ArcGIS-NCOV"
                        else -> "Channel New Asia"
                    }}"
                    BottomSheetBehavior.STATE_COLLAPSED -> tv_pull.text = getString(R.string.pull_me_up)
                    BottomSheetBehavior.STATE_DRAGGING -> {
                    }
                    BottomSheetBehavior.STATE_SETTLING -> {
                    }
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {
            }
        })


    }

    private fun initObserver() {
        viewModel.isFinishedLiveData.observe(this, Observer { data ->
            refreshLastUpdate(data)
        })

        viewModel.confirmedCaseLiveData.observe(this, Observer { data ->
            tv_infected.text = data
        })

        viewModel.confirmedDeathLiveData.observe(this, Observer { data ->
            tv_death.text = data
        })

        viewModel.confirmedRecoveredLiveData.observe(this, Observer { data ->
            tv_recovered.text = data
        })

        viewModel.currentDataSource.observe(this, Observer { data ->

            when(data){
                Constants.DATA_SOURCE.DATA_S1.value -> {
                    llRecovered.gone()
                    tvRecoverCase.gone()
                    setupRvS1()
                }
                Constants.DATA_SOURCE.DATA_S2.value -> {
                    llRecovered.visible()
                    tvRecoverCase.visible()
                    setupRvS2()
                }
                Constants.DATA_SOURCE.DATA_S3.value -> {
                    llRecovered.visible()
                    tvRecoverCase.visible()
                    setupRvS3()
                }
            }
        })

        viewModel.coronaS2LiveData.observe(this, Observer { data ->
            adapterS2?.setEntity(data)
        })

        viewModel.coronaS3LiveData.observe(this, Observer { data ->
            adapterS3?.setEntity(data)
        })

        viewModel.coronaS1LiveData.observe(this, Observer { data ->
            adapterS1?.setEntity(data.feed.entry)
        })
    }

    fun refreshLastUpdate(data: Map<String, Boolean>) {

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

        edt_search.addTextChangedListener(object: TextWatcher{
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when(preferences.getString(
                    Constants.PREF_DATA_SOURCE,
                    ""
                )){
                    Constants.DATA_SOURCE.DATA_S1.value -> {
                        adapterS1?.filter?.filter(s)
                    }
                    Constants.DATA_SOURCE.DATA_S2.value -> {
                        adapterS2?.filter?.filter(s)
                    }
                    Constants.DATA_SOURCE.DATA_S3.value -> {
                        adapterS3?.filter?.filter(s)
                    }
                }
            }

        })

        fabButton.setOnClickListener {
            startActivity<PreferenceActivity>()
        }

    }

    private fun initMap(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map ->
            mapboxMap = map
            map.uiSettings.isCompassEnabled = false

            if(preferences.getString(Constants.PREF_THEME, "2")?.toInt()!! == AppCompatDelegate.MODE_NIGHT_YES){

                map.setStyle(Style.DARK) { style ->
                    // Disable any type of fading transition when icons collide on the map. This enhances the visual
                    // look of the data clustering together and breaking apart.
                    style.transition = TransitionOptions(0, 0, false)
                    mapboxMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                39.913818,
                                116.363625
                            ), 1.0
                        )
                    )
                    addClusteredGeoJsonSource(style)
                    style.addImage(
                        "cross-icon-id",
                        BitmapUtils.getBitmapFromDrawable(resources.getDrawable(R.mipmap.ic_launcher))!!,
                        true
                    )
                }
            } else {

                map.setStyle(Style.LIGHT) { style ->
                    // Disable any type of fading transition when icons collide on the map. This enhances the visual
                    // look of the data clustering together and breaking apart.
                    style.transition = TransitionOptions(0, 0, false)
                    mapboxMap?.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                39.913818,
                                116.363625
                            ), 1.0
                        )
                    )
                    addClusteredGeoJsonSource(style)
                    style.addImage(
                        "cross-icon-id",
                        BitmapUtils.getBitmapFromDrawable(resources.getDrawable(R.mipmap.ic_launcher))!!,
                        true
                    )
                }
            }

        }
    }


    fun refreshMapData(loadedMapStyle: Style, data: List<Feature>) {

        btnRefresh.clearAnimation()

        try {

            val geoJsonSource =
                loadedMapStyle.getSourceAs<GeoJsonSource>("coronaVirus")

            if (geoJsonSource != null) {
                geoJsonSource.setGeoJson(FeatureCollection.fromFeatures(data))

            } else {
                loadedMapStyle.addSource(
                    GeoJsonSource(
                        "coronaVirus",
                        FeatureCollection.fromFeatures(data),
                        GeoJsonOptions()
                            .withCluster(true)
                            .withClusterMaxZoom(100)
                            .withClusterRadius(20)
                    )
                )
            }

        } catch (uriSyntaxException: URISyntaxException) {
            Timber.e("Check the URL %s", uriSyntaxException.message)
        }

    }

    private fun addClusteredGeoJsonSource(loadedMapStyle: Style) { // Add a new source from the GeoJSON data and set the 'cluster' option to true.

        viewModel.coronaLiveData.observe(this, Observer { data ->
            refreshMapData(loadedMapStyle, data)
        })

        // Use the coronaVirus GeoJSON source to create three layers: One layer for each cluster category.
        // Each point range gets a different fill color.
        val layers = arrayOf(
            intArrayOf(200000, Color.RED),
            intArrayOf(50000, Color.MAGENTA),
            intArrayOf(0, ContextCompat.getColor(this, R.color.mapbox_blue))
        )
        for (i in layers.indices) { //Add clusters' circles
            val circles = CircleLayer("cluster-$i", "coronaVirus")
            circles.setProperties(
                circleColor(layers[i][1]),
                circleRadius(18f)
            )
            loadedMapStyle.addLayer(circles)
        }

        //Add the count labels
        val count = SymbolLayer("count", "coronaVirus")
        count.setProperties(
            textField(toString(get("point_count"))),
            textSize(12f),
            textColor(Color.WHITE),
            textIgnorePlacement(true),
            textAllowOverlap(true)
        )
        loadedMapStyle.addLayer(count)
    }

    private fun setupRvS1() {

        adapterS1 = S1RecyclerViewAdapter(this)

        rvData.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        rvData.adapter = adapterS1

        rvData.adapter?.notifyDataSetChanged()
    }

    private fun setupRvS2() {

        adapterS2 = S2RecyclerViewAdapter(this)

        rvData.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        rvData.adapter = adapterS2

        rvData.adapter?.notifyDataSetChanged()
    }

    private fun setupRvS3() {

        adapterS3 = S3RecyclerViewAdapter(this)

        rvData.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        rvData.adapter = adapterS3

        rvData.adapter?.notifyDataSetChanged()
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }


    override fun onResume() {
        super.onResume()
        if (viewModel.isFinishedLiveData.value?.get("done") == true
        ) {
            btnRefresh.startAnimation(Utils().rotatingAnimation())
            viewModel.refreshData()
        }
        mapView.onResume()
    }


    override fun onPause() {
        super.onPause()
        mapView.onPause()
    }


    override fun onStop() {
        super.onStop()
        mapView.onStop()
    }


    override fun onLowMemory() {
        super.onLowMemory()
        mapView.onLowMemory()
    }

    override fun onDestroy() {
        super.onDestroy()
        mapView.onDestroy()
    }


    override fun onSaveInstanceState(
        outState: Bundle,
        outPersistentState: PersistableBundle
    ) {
        super.onSaveInstanceState(outState, outPersistentState)
        mapView.onSaveInstanceState(outState)
    }


}