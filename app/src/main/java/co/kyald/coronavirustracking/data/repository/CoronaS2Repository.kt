package co.kyald.coronavirustracking.data.repository

import androidx.lifecycle.MutableLiveData
import co.kyald.coronavirustracking.data.database.dao.jhu.S2CoronaDao
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaEntity
import co.kyald.coronavirustracking.data.network.category.CoronaS2Api
import co.kyald.coronavirustracking.utils.InternetChecker
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


class CoronaS2Repository(
    private val s2CoronaDao: S2CoronaDao,
    private val coronaS2Service: CoronaS2Api
) {

    val coronaLiveMapDataS2: MutableLiveData<List<Feature>> = MutableLiveData()

    var coronaLiveDataS2: MutableLiveData<List<S2CoronaEntity>> = MutableLiveData()

    var isFinished: MutableLiveData<Map<String, Boolean>> = MutableLiveData()

    var confirmCase: MutableLiveData<String> = MutableLiveData()
    var deathCase: MutableLiveData<String> = MutableLiveData()
    var recoverCase: MutableLiveData<String> = MutableLiveData()

    fun fetchCoronaDataS2(coroutineContext: CoroutineContext = Dispatchers.IO) {

        isFinished.postValue(
            mapOf(
                "done" to false,
                "internet" to false
            )
        )

        InternetChecker(object : InternetChecker.Consumer {
            override fun accept(internet: Boolean) {

                CoroutineScope(coroutineContext).launch {

                    if (internet) {

                        val response = coronaS2Service.fetchJHUCSSE()

                        if (response.isSuccessful) {
                            s2CoronaDao.deleteAll()

                            response.body()?.let { it ->
                                coronaLiveMapDataS2.postValue(buildDataConfirmed(it))
                                coronaLiveDataS2.postValue(it)
                                s2CoronaDao.save(it)
                            }

                            confirmCase.postValue(s2CoronaDao.getTotalConfirmedCase().toString())
                            deathCase.postValue(s2CoronaDao.getTotalDeathCase().toString())
                            recoverCase.postValue(s2CoronaDao.getTotalRecoveredCase().toString())
                        }

                    } else {


                        coronaLiveMapDataS2.postValue(buildDataConfirmed(s2CoronaDao.getAllCases()))
                        coronaLiveDataS2.postValue(s2CoronaDao.getAllCases())

                        confirmCase.postValue(s2CoronaDao.getTotalConfirmedCase().toString())
                        deathCase.postValue(s2CoronaDao.getTotalDeathCase().toString())
                        recoverCase.postValue(s2CoronaDao.getTotalRecoveredCase().toString())

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


    private suspend fun buildDataConfirmed(data: List<S2CoronaEntity>): MutableList<Feature> {
        val featureList: MutableList<Feature> = mutableListOf()

        // limits the scope of concurrency
        withContext(Dispatchers.IO) {

            data.forEach { value ->

                (0 until value.stats.confirmed!!.toInt()).map {

                    withContext(Dispatchers.IO) {
                        try {
                            featureList.add(
                                Feature.fromGeometry(
                                    Point.fromLngLat(value.coordinates.longitude!!.toDouble(), value.coordinates.latitude!!.toDouble())
                                )
                            )
                        } catch (nfe: NumberFormatException) {
                            featureList.add(
                                Feature.fromGeometry(
                                    Point.fromLngLat(value.coordinates.longitude!!.toDouble(), value.coordinates.latitude!!.toDouble())
                                )
                            )
                        }

                    }
                }

            }

            isFinished.postValue(
                mapOf(
                    "done" to true,
                    "internet" to true
                )
            )
        }

        return featureList

    }
}

