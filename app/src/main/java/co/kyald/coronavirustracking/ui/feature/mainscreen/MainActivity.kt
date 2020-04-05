package co.kyald.coronavirustracking.ui.feature.mainscreen

import android.animation.Animator
import android.animation.ValueAnimator
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.PointF
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
import androidx.annotation.NonNull
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.data.database.model.worldometers.S4CoronaEntity
import co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.s1adapter.S1RecyclerViewAdapter
import co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.s2adapter.S2RecyclerViewAdapter
import co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.s3adapter.S3RecyclerViewAdapter
import co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.s4adapter.S4RecyclerViewAdapter
import co.kyald.coronavirustracking.ui.feature.preferencescreen.PreferenceActivity
import co.kyald.coronavirustracking.utils.Constants
import co.kyald.coronavirustracking.utils.Utils
import co.kyald.coronavirustracking.utils.extensions.gone
import co.kyald.coronavirustracking.utils.extensions.setSafeOnClickListener
import co.kyald.coronavirustracking.utils.extensions.toSimpleString
import co.kyald.coronavirustracking.utils.extensions.visible
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.annotations.BubbleLayout
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.OnMapReadyCallback
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.plugins.annotation.SymbolManager
import com.mapbox.mapboxsdk.plugins.markerview.MarkerView
import com.mapbox.mapboxsdk.plugins.markerview.MarkerViewManager
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.Property.ICON_ANCHOR_BOTTOM
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.layers.TransitionOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import kotlinx.coroutines.GlobalScope.coroutineContext
import org.jetbrains.anko.startActivity
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.lang.NumberFormatException
import java.net.URISyntaxException
import java.util.*


class MainActivity : AppCompatActivity(),OnMapReadyCallback {

    private val viewModel: MainActivityViewModel by viewModel()

    private val preferences: SharedPreferences by inject()

    private var mapboxMap: MapboxMap? = null

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    private var adapterS1: S1RecyclerViewAdapter? = null
    private var adapterS2: S2RecyclerViewAdapter? = null
    private var adapterS3: S3RecyclerViewAdapter? = null
    private var adapterS4: S4RecyclerViewAdapter? = null

    private var featureCollection: FeatureCollection? = null
    private var source: GeoJsonSource? = null;

    private var markerViewManager: MarkerViewManager? = null
    private var marker: MarkerView? = null

    companion object {
        val GEOJSON_SOURCE_ID = "GEOJSON_SOURCE_ID"
        val CALLOUT_LAYER_ID = "CALLOUT_LAYER_ID"
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, Constants.MAPBOX_TOKEN)

        setContentView(R.layout.activity_main)

        setupRvS1()
        setupRvS2()
        setupRvS3()
        setupRvS4()

        initMap(savedInstanceState)
        initListener()
        initObserver()
        initBottomSheet()
        initView()

    }

    private fun initView() {

        btnRefresh.startAnimation(Utils().rotatingAnimation())

        when (preferences.getString(
            Constants.PREF_DATA_SOURCE,
            ""
        )) {
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

            Constants.DATA_SOURCE.DATA_S4.value -> {
                llRecovered.visible()
                tvRecoverCase.visible()
            }
        }
    }

    private fun initBottomSheet() {
        sheetBehavior = BottomSheetBehavior.from<LinearLayout>(bottom_sheet)
        sheetBehavior.addBottomSheetCallback(object : BottomSheetBehavior.BottomSheetCallback() {
            @SuppressLint("SetTextI18n")
            override fun onStateChanged(bottomSheet: View, newState: Int) {
                when (newState) {
                    BottomSheetBehavior.STATE_HIDDEN -> {
                    }
                    BottomSheetBehavior.STATE_EXPANDED -> tv_pull.text =
                        "${getString(R.string.data_provided)} ${

                        when (preferences.getString(
                            Constants.PREF_DATA_SOURCE,
                            Constants.DATA_SOURCE.DATA_S4.value
                        )) {
                            Constants.DATA_SOURCE.DATA_S1.value -> "Channel New Asia"
                            Constants.DATA_SOURCE.DATA_S2.value -> "Johns Hopkins CSSE"
                            Constants.DATA_SOURCE.DATA_S3.value -> "ArcGIS-NCOV"
                            Constants.DATA_SOURCE.DATA_S4.value -> "Worldometers"
                            else -> "Channel New Asia"
                        }}"
                    BottomSheetBehavior.STATE_COLLAPSED -> tv_pull.text =
                        getString(R.string.pull_me_up)
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

            when (data) {
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
                Constants.DATA_SOURCE.DATA_S4.value -> {
                    llRecovered.visible()
                    tvRecoverCase.visible()
                    setupRvS4()
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
        viewModel.coronaS4LiveData.observe(this, Observer { data ->
            adapterS4?.setEntity(data)

//            CoroutineScope(coroutineContext).launch {
                setupMarker(data)
//            }
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

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                when (preferences.getString(
                    Constants.PREF_DATA_SOURCE,
                    ""
                )) {
                    Constants.DATA_SOURCE.DATA_S1.value -> {
                        adapterS1?.filter?.filter(s)
                    }
                    Constants.DATA_SOURCE.DATA_S2.value -> {
                        adapterS2?.filter?.filter(s)
                    }
                    Constants.DATA_SOURCE.DATA_S3.value -> {
                        adapterS3?.filter?.filter(s)
                    }
                    Constants.DATA_SOURCE.DATA_S4.value -> {
                        adapterS4?.filter?.filter(s)
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
        mapView.getMapAsync(this)
    }

    private fun setupData() {
        if (mapboxMap != null) {
            mapboxMap!!.getStyle { style ->
                setupSource(style)
                markerViewManager = MarkerViewManager(mapView, mapboxMap)

            }
        }
    }

    private fun setupMarker(data: List<S4CoronaEntity>) {
        btnRefresh.startAnimation(Utils().rotatingAnimation())

            data.forEach { it ->
                val customView = createCustomAnimationView(it)
                marker = MarkerView(
                    try {
                        LatLng(it.countryInfo.info_lat!!, it.countryInfo.info_long!!)
                    } catch (e: NumberFormatException) {
                        LatLng()
                    }
                    , customView
                )
                marker?.let {
                    markerViewManager?.addMarker(it)
                }

            }

            btnRefresh.clearAnimation()


    }


    private fun refreshSource(): Boolean {
        if (source != null && featureCollection != null) {
            source!!.setGeoJson(featureCollection);
            return true
        }

        return false
    }

    private fun createCustomAnimationView(it: S4CoronaEntity): View {
        val customView = LayoutInflater.from(this).inflate(R.layout.marker_view, null)
        customView.layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        val icon = customView.findViewById<View>(R.id.imageview)
        val animationView = customView.findViewById<View>(R.id.animation_layout)
        val txtCountry = customView.findViewById<TextView>(R.id.country)
        val txtActiveCase = customView.findViewById<TextView>(R.id.active_case)
        val txtConfirmCase = customView.findViewById<TextView>(R.id.confirm_case)
        val txtDeathCase = customView.findViewById<TextView>(R.id.death_case)
        val txtRecoverdCase = customView.findViewById<TextView>(R.id.recovered_case)

        txtCountry.text = it.country
        txtActiveCase.text = "Active: ${it.active}"
        txtConfirmCase.text = "Confirm: ${it.cases}"
        txtDeathCase.text = "Death: ${it.deaths}"
        txtRecoverdCase.text = "Recovered: ${it.recovered}"

        icon.setOnClickListener {

            animationView.measure(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val targetHeight =
                if (animationView.layoutParams.height == 0) animationView.measuredHeight else 0
            val targetWidth =
                if (animationView.layoutParams.width == 0) animationView.measuredWidth else 0

            val anim = ValueAnimator.ofInt(animationView.measuredHeight, targetHeight);

            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.addUpdateListener { valueAnimator ->
                val `val` = valueAnimator.animatedValue as Int
                val layoutParams = animationView.layoutParams
                layoutParams.width = ((targetWidth * valueAnimator.animatedFraction).toInt());
                layoutParams.height = ((targetHeight * valueAnimator.animatedFraction).toInt());
                animationView.layoutParams = layoutParams
            }
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    val layoutParams = animationView.layoutParams;
                    layoutParams.height = WRAP_CONTENT;

                }

                override fun onAnimationCancel(animation: Animator?) {
                }

                override fun onAnimationStart(animation: Animator?) {
                }

            })

            anim.duration = 700
            anim.start()
        }
        return customView
    }

    override fun onMapReady(map: MapboxMap) {
        this.mapboxMap = map
        map.setStyle(
            when (preferences.getString(Constants.PREF_THEME, "2")?.toInt()!!) {
                AppCompatDelegate.MODE_NIGHT_YES -> Style.DARK
                AppCompatDelegate.MODE_NIGHT_NO -> Style.LIGHT
                else -> Style.DARK
            }
        ) { style ->
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

            viewModel.coronaLiveData.observe(this, Observer { data ->

                this.featureCollection = FeatureCollection.fromFeatures(data)

                if (!refreshSource()) {
                    setupData()
                }
            })

        }
    }

    private fun setupSource(loadedMapStyle: Style) {
        btnRefresh.clearAnimation()
        source = GeoJsonSource(GEOJSON_SOURCE_ID, featureCollection)

        try {

            loadedMapStyle.addSource(
                source!!
            )

        } catch (uriSyntaxException: URISyntaxException) {
            Timber.e("Check the URL %s", uriSyntaxException.message)
        }

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

    private fun setupRvS4() {

        adapterS4 = S4RecyclerViewAdapter(this)

        rvData.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        rvData.adapter = adapterS4

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