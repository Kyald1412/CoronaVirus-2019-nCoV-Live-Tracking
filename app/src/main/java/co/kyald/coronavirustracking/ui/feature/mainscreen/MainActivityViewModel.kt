package co.kyald.coronavirustracking.ui.feature.mainscreen

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.kyald.coronavirustracking.data.model.CoronaEntity
import co.kyald.coronavirustracking.data.model.CountryCoordEntity
import co.kyald.coronavirustracking.data.repository.CoronaRepository
import co.kyald.coronavirustracking.utils.InternetChecker
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import kotlinx.coroutines.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import kotlin.coroutines.CoroutineContext


class MainActivityViewModel(
    private val coronaRepository: CoronaRepository
) : ViewModel() {

    var access_token: String = ""

    val coronaCountryList: MutableList<String> = mutableListOf()
    val coronaLngLatList: MutableList<Feature> = mutableListOf()

    var coronaDataIsFinished: MutableLiveData<Map<String, Boolean>> = MutableLiveData()
    val coronaLiveData: MutableLiveData<CoronaEntity> = MutableLiveData()

    fun fetchcoronaData(coroutineContext: CoroutineContext, forceUpdate: Boolean) {

        if (coronaLngLatList.isNullOrEmpty() || forceUpdate) {

            if (forceUpdate) {
                coronaCountryList.clear()
                coronaLngLatList.clear()
            }

            coronaDataIsFinished.postValue(mapOf("done" to false, "internet" to false))

            InternetChecker(object : InternetChecker.Consumer {
                override fun accept(internet: Boolean) {

                    if (internet) {
                        CoroutineScope(coroutineContext).launch {

                            val newData = coronaRepository.fetchAll()

                            newData?.let {

                                coronaRepository.deleteCountryCoord()
                                coronaRepository.saveCoronaData(it)

                                coronaLiveData.postValue(newData)

                                for (i in it.feed.entry.indices) {

                                    getLngLatFromCountryName(it.feed.entry[i].gsxcountry.t)
                                }
                            }


                        }

                    } else {
                        CoroutineScope(coroutineContext).launch {

                            (coronaRepository.getCountryCoord()).map {
                                coronaCountryList.add(it.country)
                                coronaLngLatList.add(
                                    Feature.fromGeometry(
                                        Point.fromLngLat(it.longitude, it.latitude)
                                    )
                                )
                            }


                            coronaLiveData.postValue(coronaRepository.getAll())

                            coronaDataIsFinished.postValue(
                                mapOf(
                                    "done" to true,
                                    "internet" to false
                                )
                            )
                        }
                    }
                }
            })
        }
    }

    fun getLngLatFromCountryName(countryName: String) {
        val client = MapboxGeocoding.builder()
            .accessToken(access_token)
            .query(countryName)
            .geocodingTypes(GeocodingCriteria.TYPE_COUNTRY)
            .mode(GeocodingCriteria.MODE_PLACES)
            .build()

        client.enqueueCall(object : Callback<GeocodingResponse> {
            override fun onFailure(call: Call<GeocodingResponse>, t: Throwable) {
                Timber.e("onFailure ${t.localizedMessage}")
            }

            override fun onResponse(
                call: Call<GeocodingResponse>,
                response: Response<GeocodingResponse>
            ) {

                if (response.body() != null) {
                    val results = response.body()!!.features()
                    if (results.size > 0) {

                        val feature = results[0]

                        coronaLngLatList.add(
                            Feature.fromGeometry(feature.geometry(), feature.properties())
                        )
                        coronaCountryList.add(countryName)

                        GlobalScope.launch {
                            coronaRepository.saveCountryCoord(
                                CountryCoordEntity(
                                    id = 0,
                                    country = feature?.placeName().toString(),
                                    latitude = Point.fromJson(feature.geometry()?.toJson().toString()).latitude(),
                                    longitude = Point.fromJson(feature.geometry()?.toJson().toString()).longitude()
                                )
                            )

                            //Verify country list data with corona data source
                            if (coronaLngLatList.size == coronaLiveData.value?.feed?.entry?.size) {
                                coronaDataIsFinished.postValue(
                                    mapOf(
                                        "done" to true,
                                        "internet" to true
                                    )
                                )
                            }
                        }
                    }
                }
            }
        })
    }


    suspend fun buildData(): MutableList<Feature> {
        val featureList: MutableList<Feature> = mutableListOf()

        // limits the scope of concurrency
        withContext(Dispatchers.IO) {

            (coronaLiveData.value?.feed?.entry!!.indices).map { entry ->

                (0 until coronaCountryList.size).map { city ->

                    if (coronaCountryList[city] == coronaLiveData.value?.feed?.entry!![entry].gsxcountry.t) {


//                        Timber.e(("IS EQUAL ${viewModel.coronaCountryList[city]}"))

                        (0 until coronaLiveData.value?.feed?.entry!![entry].gsxconfirmedcases.t.toInt()).map {

                            withContext(Dispatchers.IO) {
                                // async means "concurrently", context goes here

                                featureList.add(
                                    coronaLngLatList[city]
                                )

                            }
                        }
                    }

                }

            }


        }

        return featureList

    }
}