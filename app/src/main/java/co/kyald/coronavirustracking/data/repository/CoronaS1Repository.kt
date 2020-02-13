package co.kyald.coronavirustracking.data.repository

import androidx.lifecycle.MutableLiveData
import co.kyald.coronavirustracking.data.database.model.chnasia.S1CoronaEntity
import co.kyald.coronavirustracking.data.database.dao.chnasia.S1CoronaDao
import co.kyald.coronavirustracking.data.database.model.chnasia.S1CountryCoordEntity
import co.kyald.coronavirustracking.data.network.category.CoronaS1Api
import co.kyald.coronavirustracking.utils.Constants
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

class CoronaS1Repository(
    private val s1CoronaDao: S1CoronaDao,
    private val coronaS1Service: CoronaS1Api
) {

    val s1CoronaData: MutableLiveData<S1CoronaEntity> = MutableLiveData()

    private val countryNameList: MutableList<String> = mutableListOf()
    private val longLatList: MutableList<Feature> = mutableListOf()

    val coronaLiveData: MutableLiveData<List<Feature>> = MutableLiveData()
    var isFinished: MutableLiveData<Map<String, Boolean>> = MutableLiveData()

    var confirmCase: MutableLiveData<String> = MutableLiveData()
    var deathCase: MutableLiveData<String> = MutableLiveData()

    private fun getAllCoronaDataS1(): S1CoronaEntity = s1CoronaDao.getAllCoronaDataS1()
    fun getJsonEntryS1(): String = s1CoronaDao.getJsonEntryS1()
    private fun getCountryCoordS1(): List<S1CountryCoordEntity> = s1CoronaDao.getCountryCoordS1()

    private suspend fun saveCoronaDataS1(entities: S1CoronaEntity) =
        s1CoronaDao.saveCoronaDataS1(entities)

    private suspend fun saveCountryCoordS1(coordJsonS1: S1CountryCoordEntity) =
        s1CoronaDao.saveCountryCoordS1(coordJsonS1)

    private suspend fun deleteCountryCoordS1() = s1CoronaDao.deleteAllCountryCoordS1()

    suspend fun callNetwork(): S1CoronaEntity? {

        val response = coronaS1Service.fetchCoronaS1(Constants.S1_CORONA_API_KEY)

        if (response.isSuccessful) {
            response.body()?.let {
                return it
            }
        }

        return null
    }

    fun fetchCoronaDataS1(coroutineContext: CoroutineContext, forceUpdate: Boolean) {

        if (longLatList.isNullOrEmpty() || forceUpdate) {

            if (forceUpdate) {
                countryNameList.clear()
                longLatList.clear()
            }

            isFinished.postValue(mapOf("done" to false, "internet" to false))

            InternetChecker(object : InternetChecker.Consumer {
                override fun accept(internet: Boolean) {

                    if (internet) {
                        CoroutineScope(coroutineContext).launch {

                            val newData = callNetwork()

                            newData?.let {

                                deleteCountryCoordS1()
                                saveCoronaDataS1(it)

                                s1CoronaData.postValue(it)

                                for (i in it.feed.entry.indices) {

                                    getLngLatFromCountryName(it.feed.entry[i].gsxcountry.t)
                                }
                            }

                        }

                    } else {
                        CoroutineScope(coroutineContext).launch {

                            (getCountryCoordS1()).map {
                                countryNameList.add(it.country)
                                longLatList.add(
                                    Feature.fromGeometry(
                                        Point.fromLngLat(it.longitude, it.latitude)
                                    )
                                )
                            }

                            s1CoronaData.postValue(getAllCoronaDataS1())

                            coronaLiveData.postValue(buildData())

                            isFinished.postValue(
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

    private fun getLngLatFromCountryName(countryName: String) {
        val client = MapboxGeocoding.builder()
            .accessToken(Constants.MAPBOX_TOKEN)
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

                        longLatList.add(
                            Feature.fromGeometry(feature.geometry(), feature.properties())
                        )
                        countryNameList.add(countryName)

                        GlobalScope.launch {
                            saveCountryCoordS1(
                                S1CountryCoordEntity(
                                    id = 0,
                                    country = feature?.placeName().toString(),
                                    latitude = Point.fromJson(feature.geometry()?.toJson().toString()).latitude(),
                                    longitude = Point.fromJson(feature.geometry()?.toJson().toString()).longitude()
                                )
                            )

                            Timber.e("longLatList COUNT ${longLatList.size}")
                            Timber.e("s1CoronaData.value?.feed?.entry!!.size ${s1CoronaData.value?.feed?.entry!!.size-1}")

                            //Verify country list data with corona data source
                            if (longLatList.size == s1CoronaData.value?.feed?.entry!!.size - 1) {


                                coronaLiveData.postValue(buildData())
                                isFinished.postValue(
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

    private suspend fun buildData(): MutableList<Feature> {
        val featureList: MutableList<Feature> = mutableListOf()
        var confirmedCount = 0
        var deathCount = 0

        Timber.e("DEATH COUNT 123")


        Timber.e("CONFIRM COUNT $confirmedCount")
        Timber.e("DEATH COUNT $deathCount")

        // limits the scope of concurrency
        withContext(Dispatchers.IO) {

            (s1CoronaData.value?.feed?.entry?.indices)?.map { entry ->

                confirmedCount += if (s1CoronaData.value?.feed?.entry!![entry].gsxconfirmedcases.t == "") 0 else s1CoronaData.value?.feed?.entry!![entry].gsxconfirmedcases.t.toInt()
                deathCount += if (s1CoronaData.value?.feed?.entry!![entry].gsxreporteddeaths.t == "") 0 else s1CoronaData.value?.feed?.entry!![entry].gsxreporteddeaths.t.toInt()
//
                (0 until countryNameList.size).map { city ->

                    if (countryNameList[city] == s1CoronaData.value?.feed?.entry!![entry].gsxcountry.t) {

                        (0 until s1CoronaData.value?.feed?.entry!![entry].gsxconfirmedcases.t.toInt()).map {

                            withContext(Dispatchers.IO) {

                                featureList.add(
                                    longLatList[city]
                                )

                            }
                        }
                    }

                }

            }

        }



        confirmCase.postValue(confirmedCount.toString())
        deathCase.postValue(deathCount.toString())

        return featureList

    }
}
