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
import co.kyald.coronavirustracking.ui.feature.mainscreen.bottomsheet.CoronaRecyclerViewAdapter
import co.kyald.coronavirustracking.ui.feature.preferencescreen.PreferenceActivity
import co.kyald.coronavirustracking.utils.Constants
import co.kyald.coronavirustracking.utils.Utils
import co.kyald.coronavirustracking.utils.extensions.gone
import co.kyald.coronavirustracking.utils.extensions.setSafeOnClickListener
import co.kyald.coronavirustracking.utils.extensions.toSimpleString
import co.kyald.coronavirustracking.utils.extensions.visible
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
import org.koin.android.ext.android.inject
import org.koin.androidx.viewmodel.ext.android.viewModel
import timber.log.Timber
import java.util.*


class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by viewModel()

    private val preferences: SharedPreferences by inject()

    private var mapboxMap: MapboxMap? = null

    private lateinit var sheetBehavior: BottomSheetBehavior<LinearLayout>

    private var dataAdapter: CoronaRecyclerViewAdapter? = null

    private var markerViewManager: MarkerViewManager? = null
    private var marker: MutableList<MarkerView> = mutableListOf()

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, Constants.MAPBOX_TOKEN)

        setContentView(R.layout.activity_main)

        setupRvData()

        initMap(savedInstanceState)
        initListener()
        initObserver()
        initBottomSheet()
        initView()

    }

    private fun initView() {

        btnRefresh.startAnimation(Utils().rotatingAnimation())
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
                    BottomSheetBehavior.STATE_HALF_EXPANDED -> {
                        TODO()
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

        viewModel.totalCases.observe(this, Observer { data ->

            tv_infected.text = data.getValue("confirmed")
            tv_death.text = data.getValue("deaths")
            tv_recovered.text = data.getValue("recovered")
        })

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

        edt_search.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                dataAdapter?.filter?.filter(s)
            }

        })

        fabButton.setOnClickListener {
            startActivityForResult(Intent(this, PreferenceActivity::class.java), 20)
        }

    }

    private fun initMap(savedInstanceState: Bundle?) {
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map ->
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

                Timber.e("CALLED HOW MANY TIMES?")

                viewModel.coronaLiveData.observe(this, Observer { data ->
                    dataAdapter?.setEntity(data)

                    setupMarker(data)
                })

                markerViewManager = MarkerViewManager(mapView, mapboxMap)

            }
        }
    }

    private fun setupMarker(data: List<CoronaEntity>) {
        btnRefresh.startAnimation(Utils().rotatingAnimation())

        if (markerViewManager != null || marker.size > 0) {

            marker.forEach {
                markerViewManager?.removeMarker(it)
            }

            markerViewManager?.onDestroy()
        }

        data.forEach {
            val customView = createCustomAnimationView(it)
            val markerView = MarkerView(
                LatLng(it.info.latitude!!, it.info.longitude!!)
                , customView
            )

            marker.add(markerView)

            marker.let {
                markerViewManager?.addMarker(markerView)
            }
        }

        btnRefresh.clearAnimation()

    }

    private fun createCustomAnimationView(it: CoronaEntity)
            : View {
        val customView = LayoutInflater.from(this).inflate(R.layout.marker_view, null)
        customView.layoutParams = FrameLayout.LayoutParams(WRAP_CONTENT, WRAP_CONTENT)
        val icon = customView.findViewById<View>(R.id.imageview)
        val animationView = customView.findViewById<View>(R.id.animation_layout)
        val txtCountry = customView.findViewById<TextView>(R.id.country)
        val txtActiveCase = customView.findViewById<TextView>(R.id.active_case)
        val txtConfirmCase = customView.findViewById<TextView>(R.id.confirm_case)
        val txtDeathCase = customView.findViewById<TextView>(R.id.death_case)
        val txtRecoverdCase = customView.findViewById<TextView>(R.id.recovered_case)

        txtCountry.text = it.info.country

        when (preferences.getString(
            Constants.PREF_DATA_SOURCE,
            Constants.DATA_SOURCE.DATA_S4.value
        )) {
            Constants.DATA_SOURCE.DATA_S1.value -> txtActiveCase.gone()
            Constants.DATA_SOURCE.DATA_S2.value -> txtActiveCase.gone()
            Constants.DATA_SOURCE.DATA_S3.value -> txtActiveCase.gone()
            Constants.DATA_SOURCE.DATA_S4.value -> txtActiveCase.visible()
        }
        txtActiveCase.text = "${getString(R.string.info_active)} ${it.info.case_actives}"
        txtConfirmCase.text = "${getString(R.string.info_confirm)} ${it.info.case_confirms}"
        txtDeathCase.text = "${getString(R.string.info_death)} ${it.info.case_deaths}"
        txtRecoverdCase.text = "${getString(R.string.info_recovered)} ${it.info.case_recovered}"

        icon.setOnClickListener {

            animationView.measure(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

            val targetHeight =
                if (animationView.layoutParams.height == 0) animationView.measuredHeight else 0
            val targetWidth =
                if (animationView.layoutParams.width == 0) animationView.measuredWidth else 0

            val anim = ValueAnimator.ofInt(animationView.measuredHeight, targetHeight)

            anim.interpolator = AccelerateDecelerateInterpolator()
            anim.addUpdateListener { valueAnimator ->
                val layoutParams = animationView.layoutParams
                layoutParams.width =
                    ((targetWidth * valueAnimator.animatedFraction).toInt())
                layoutParams.height =
                    ((targetHeight * valueAnimator.animatedFraction).toInt())
                animationView.layoutParams = layoutParams
            }
            anim.addListener(object : Animator.AnimatorListener {
                override fun onAnimationRepeat(animation: Animator?) {
                }

                override fun onAnimationEnd(animation: Animator?) {
                    val layoutParams = animationView.layoutParams
                    layoutParams.height = WRAP_CONTENT

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

    private fun setupRvData() {

        dataAdapter = CoronaRecyclerViewAdapter(this)

        rvData.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            setHasFixedSize(true)
        }

        rvData.adapter = dataAdapter

        rvData.adapter?.notifyDataSetChanged()
    }


    override fun onStart() {
        super.onStart()
        mapView.onStart()
    }


    override fun onResume() {
        super.onResume()

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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        Timber.e("RESULT CODE $resultCode")

        when (resultCode) {

            Activity.RESULT_OK -> {
                if (viewModel.isFinishedLiveData.value?.get("done") == true
                ) {
                    btnRefresh.startAnimation(Utils().rotatingAnimation())
                    viewModel.refreshData()
                }
            }

        }

        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onSaveInstanceState(
        outState: Bundle,
        outPersistentState: PersistableBundle
    ) {
        super.onSaveInstanceState(outState, outPersistentState)
        mapView.onSaveInstanceState(outState)
    }


}