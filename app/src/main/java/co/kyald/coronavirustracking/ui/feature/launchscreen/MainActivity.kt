package co.kyald.coronavirustracking.ui.feature.launchscreen

import android.content.DialogInterface
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.Settings
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import co.kyald.coronavirustracking.R
import co.kyald.coronavirustracking.data.model.CoronaEntity
import co.kyald.coronavirustracking.utils.extensions.gone
import co.kyald.coronavirustracking.utils.extensions.visible
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.mapboxsdk.Mapbox
import com.mapbox.mapboxsdk.camera.CameraUpdateFactory
import com.mapbox.mapboxsdk.geometry.LatLng
import com.mapbox.mapboxsdk.maps.MapboxMap
import com.mapbox.mapboxsdk.maps.Style
import com.mapbox.mapboxsdk.style.expressions.Expression.*
import com.mapbox.mapboxsdk.style.layers.CircleLayer
import com.mapbox.mapboxsdk.style.layers.PropertyFactory.*
import com.mapbox.mapboxsdk.style.layers.SymbolLayer
import com.mapbox.mapboxsdk.style.layers.TransitionOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonOptions
import com.mapbox.mapboxsdk.style.sources.GeoJsonSource
import com.mapbox.mapboxsdk.utils.BitmapUtils
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*
import org.koin.android.ext.android.inject
import timber.log.Timber
import java.net.URISyntaxException


class MainActivity : AppCompatActivity() {

    private val viewModel: MainActivityViewModel by inject()

    private var mapboxMap: MapboxMap? = null

    private var isShown: Boolean = false

    private lateinit var adapterMain: MainRecyclerViewAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        Mapbox.getInstance(this, getString(R.string.access_token))

        setContentView(R.layout.activity_main)

        viewModel.access_token = getString(R.string.access_token)

        viewModel.fetchcoronaData().observe(this, Observer { data ->
            if (data != null) {

                loadEntry(data.feed.entry)
            }
        })

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync { map ->
            mapboxMap = map
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


        cardCountryData.setOnClickListener {

            if (!isShown) {
                rvCountry.visible()
                txtShowhide.text = getString(R.string.hide_data)
            } else {
                rvCountry.gone()
                txtShowhide.text = getString(R.string.show_data)
            }

            isShown = !isShown
        }

        fabButton.addOnMenuItemClickListener { fab, textView, itemId ->
            // do something

            when (itemId) {
                R.id.lang -> {
                    val mIntent = Intent(Settings.ACTION_LOCALE_SETTINGS)
                    startActivity(mIntent)
                }

                R.id.one -> {
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data =
                        Uri.parse(getString(R.string.coronavirus_definition))
                    startActivity(openURL)
                }

                R.id.two -> {
                    val openURL = Intent(Intent.ACTION_VIEW)
                    openURL.data =
                        Uri.parse(getString(R.string.coronavirus_prevention))
                    startActivity(openURL)
                }

                R.id.three -> {
                    aboutAlert()
                }

            }
        }

        setupAdapter()

    }

    private fun aboutAlert() {

        val builder = AlertDialog.Builder(this)

        with(builder)
        {
            setTitle("About")
            setMessage("Hello, got any ideas?, don't hesitate to contact me \n\ndhikyaldwiansyah@gmail.com\n\nData scraped from:\n- infographics.channelnewsasia.com\n- JHU CSSE")
            setPositiveButton("OK") { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            show()
        }

    }

    private fun setupAdapter() {
        adapterMain =
            MainRecyclerViewAdapter(this) { entry: CoronaEntity.Entry -> entryItemClicked(entry) }

        rvCountry.adapter = adapterMain
        rvCountry.layoutManager = LinearLayoutManager(this)
        rvCountry.setHasFixedSize(true)
    }


    private fun loadEntry(coronaEntity: List<CoronaEntity.Entry>) {
        adapterMain.setEntity(coronaEntity)
    }


    private fun entryItemClicked(entryData: CoronaEntity.Entry) {
    }


    private suspend fun buildData(): MutableList<Feature> {
        val featureList: MutableList<Feature> = mutableListOf()

        // limits the scope of concurrency
        withContext(Dispatchers.IO) {

            (viewModel.coronaLiveData.value?.feed?.entry!!.indices).map { entry ->

                (0 until viewModel.coronaCountryList.size).map { city ->

                    if (viewModel.coronaCountryList[city] == viewModel.coronaLiveData.value?.feed?.entry!![entry].gsxcountry.t) {


                        Timber.e(("IS EQUAL ${viewModel.coronaCountryList[city]}"))

                        (0 until viewModel.coronaLiveData.value?.feed?.entry!![entry].gsxconfirmedcases.t.toInt()).map {

                            withContext(Dispatchers.IO) {
                                // async means "concurrently", context goes here

                                featureList.add(
                                    viewModel.coronaLngLatList[city]
                                )

                            }
                        }
                    }

                }

            }


        }

        return featureList

    }

    private fun addClusteredGeoJsonSource(loadedMapStyle: Style) { // Add a new source from the GeoJSON data and set the 'cluster' option to true.
        try {

            viewModel.coronaDataIsFinished.observe(this, Observer { data ->
                if (data == true) {
                    GlobalScope.launch {

                        val featuresData =
                            withContext(Dispatchers.Default) { buildData() }

                        withContext(Dispatchers.Main) {

                            loadedMapStyle.addSource(
                                GeoJsonSource(
                                    "coronaVirus",
                                    FeatureCollection.fromFeatures(featuresData),
                                    GeoJsonOptions()
                                        .withCluster(true)
                                        .withClusterMaxZoom(100)
                                        .withClusterRadius(20)
                                )
                            )
                        }
                    }
                }
            })


        } catch (uriSyntaxException: URISyntaxException) {
            Timber.e("Check the URL %s", uriSyntaxException.message)
        }

        // Use the coronaVirus GeoJSON source to create three layers: One layer for each cluster category.
        // Each point range gets a different fill color.
        val layers = arrayOf(
            intArrayOf(150, ContextCompat.getColor(this, R.color.colorPrimary)),
            intArrayOf(20, ContextCompat.getColor(this, R.color.colorAccent)),
            intArrayOf(0, ContextCompat.getColor(this, R.color.mapbox_blue))
        )
        for (i in layers.indices) { //Add clusters' circles
            val circles = CircleLayer("cluster-$i", "coronaVirus")
            circles.setProperties(
                circleColor(layers[i][1]),
                circleRadius(18f)
            )
//            val pointCount: Expression = toNumber(get("point_count"))
            // Add a filter to the cluster layer that hides the circles based on "point_count"
//            circles.setFilter(
//                if (i == 0) all(
//                    has("point_count"),
//                    gte(pointCount, literal(layers[i][0]))
//                ) else all(
//                    has("point_count"),
//                    gte(pointCount, literal(layers[i][0])),
//                    lt(pointCount, literal(layers[i - 1][0]))
//                )
//            )
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

    override fun onSaveInstanceState(
        outState: Bundle,
        outPersistentState: PersistableBundle
    ) {
        super.onSaveInstanceState(outState, outPersistentState)
        mapView.onSaveInstanceState(outState)
    }

}