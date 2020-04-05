package co.kyald.coronavirustracking.data.repository

import androidx.lifecycle.MutableLiveData
import co.kyald.coronavirustracking.data.database.dao.worldometers.S4CoronaDao
import co.kyald.coronavirustracking.data.database.model.worldometers.S4CoronaEntity
import co.kyald.coronavirustracking.data.network.category.CoronaS2Api
import co.kyald.coronavirustracking.utils.InternetChecker
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


class CoronaS4Repository(
    private val s4CoronaDao: S4CoronaDao,
    private val coronaS4Service: CoronaS2Api
) {

    val coronaLiveMapDataS4: MutableLiveData<List<Feature>> = MutableLiveData()

    var coronaLiveDataS4: MutableLiveData<List<S4CoronaEntity>> = MutableLiveData()

    var isFinished: MutableLiveData<Map<String, Boolean>> = MutableLiveData()

    var confirmCase: MutableLiveData<String> = MutableLiveData()
    var deathCase: MutableLiveData<String> = MutableLiveData()
    var recoverCase: MutableLiveData<String> = MutableLiveData()

    fun getCoronaDataS4(): List<S4CoronaEntity> = s4CoronaDao.getAllCases()

    suspend fun callCoronaS4Data() = coronaS4Service.fetchWorlOdMeters()

    fun fetchCoronaDataS4(coroutineContext: CoroutineContext = Dispatchers.IO) {

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

                        val caseResponse = callCoronaS4Data()

                        if (caseResponse.isSuccessful) {
                            s4CoronaDao.deleteAll()

                            caseResponse.body()?.let { it ->
                                coronaLiveMapDataS4.postValue(buildDataConfirmed(it))
                                coronaLiveDataS4.postValue(it)
                                s4CoronaDao.save(it)
                            }

                        }

                        confirmCase.postValue(s4CoronaDao.getTotalConfirmedCase().toString())
                        deathCase.postValue(s4CoronaDao.getTotalDeathCase().toString())
                        recoverCase.postValue(s4CoronaDao.getTotalRecoveredCase().toString())


                    } else {


                        coronaLiveMapDataS4.postValue(buildDataConfirmed(s4CoronaDao.getAllCases()))
                        coronaLiveDataS4.postValue(s4CoronaDao.getAllCases())

                        confirmCase.postValue(s4CoronaDao.getTotalConfirmedCase().toString())
                        deathCase.postValue(s4CoronaDao.getTotalDeathCase().toString())
                        recoverCase.postValue(s4CoronaDao.getTotalRecoveredCase().toString())

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


    private suspend fun buildDataConfirmed(data: List<S4CoronaEntity>): MutableList<Feature> {
        val featureList: MutableList<Feature> = mutableListOf()

        // limits the scope of concurrency
        withContext(Dispatchers.IO) {

            data.forEach { value ->

                withContext(Dispatchers.IO) {
                    try {
                        featureList.add(
                            Feature.fromGeometry(
                                Point.fromLngLat(
                                    value.countryInfo.info_long!!.toDouble(),
                                    value.countryInfo.info_lat!!.toDouble()
                                )
                            )
                        )
                    } catch (nfe: NumberFormatException) {
                        featureList.add(
                            Feature.fromGeometry(
                                Point.fromLngLat(0.0, 0.0)
                            )
                        )
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

