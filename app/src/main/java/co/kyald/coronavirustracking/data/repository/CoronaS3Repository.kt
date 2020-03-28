package co.kyald.coronavirustracking.data.repository

import androidx.lifecycle.MutableLiveData
import co.kyald.coronavirustracking.data.database.dao.arcgis.S3CoronaDao
import co.kyald.coronavirustracking.data.database.model.arcgis.S3CoronaEntity
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaEntity
import co.kyald.coronavirustracking.data.network.category.CoronaS3Api
import co.kyald.coronavirustracking.utils.InternetChecker
import com.mapbox.geojson.Feature
import com.mapbox.geojson.Point
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


class CoronaS3Repository(
    private val s3CoronaDao: S3CoronaDao,
    private val coronaS3Service: CoronaS3Api
) {

    val coronaLiveMapDataS3: MutableLiveData<List<Feature>> = MutableLiveData()

    var coronaLiveDataS3: MutableLiveData<List<S3CoronaEntity>> = MutableLiveData()

    var isFinished: MutableLiveData<Map<String, Boolean>> = MutableLiveData()

    var confirmCase: MutableLiveData<String> = MutableLiveData()
    var deathCase: MutableLiveData<String> = MutableLiveData()
    var recoverCase: MutableLiveData<String> = MutableLiveData()

    fun getCoronaDataS3(): List<S3CoronaEntity> = s3CoronaDao.getAllCases()

    suspend fun callCoronaS3Data() = coronaS3Service.fetchArcGIS()

    fun fetchCoronaDataS3(coroutineContext: CoroutineContext = Dispatchers.IO) {

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

                        val caseResponse = callCoronaS3Data()

                        if (caseResponse.isSuccessful) {
                            s3CoronaDao.deleteAll()

                            caseResponse.body()?.let { it ->
                                coronaLiveMapDataS3.postValue(buildDataConfirmed(it.features))
                                coronaLiveDataS3.postValue(it.features)
                                s3CoronaDao.save(it.features)
                            }

                        }

                        val confirmCaseResponse = coronaS3Service.fetchArcGISConfirmed()
                        if(confirmCaseResponse.isSuccessful){
                            confirmCaseResponse.body()?.let {
                                confirmCase.postValue(it.features[0].attributes.value.toString())
                            }
                        }
                        val deathCaseResponse = coronaS3Service.fetchArcGISDeaths()
                        if(deathCaseResponse.isSuccessful){
                            deathCaseResponse.body()?.let {
                                deathCase.postValue(it.features[0].attributes.value.toString())
                            }
                        }
                        val recoveredCaseResponse = coronaS3Service.fetchArcGISRecovered()
                        if(recoveredCaseResponse.isSuccessful){
                            recoveredCaseResponse.body()?.let {
                                recoverCase.postValue(it.features[0].attributes.value.toString())
                            }
                        }


                    } else {


                        coronaLiveMapDataS3.postValue(buildDataConfirmed(s3CoronaDao.getAllCases()))
                        coronaLiveDataS3.postValue(s3CoronaDao.getAllCases())

                        confirmCase.postValue(s3CoronaDao.getTotalConfirmedCase().toString())
                        deathCase.postValue(s3CoronaDao.getTotalDeathCase().toString())
                        recoverCase.postValue(s3CoronaDao.getTotalRecoveredCase().toString())

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


    private suspend fun buildDataConfirmed(data: List<S3CoronaEntity>): MutableList<Feature> {
        val featureList: MutableList<Feature> = mutableListOf()

        // limits the scope of concurrency
        withContext(Dispatchers.IO) {

            data.forEach { value ->

                (0 until value.attributes.confirmed!!.toInt()).map {

                    withContext(Dispatchers.IO) {
                        try {
                            featureList.add(
                                Feature.fromGeometry(
                                    Point.fromLngLat(value.attributes.long!!.toDouble(), value.attributes.lat!!.toDouble())
                                )
                            )
                        } catch (nfe: NumberFormatException) {
                            featureList.add(
                                Feature.fromGeometry(
                                    Point.fromLngLat(value.attributes.long!!.toDouble(), value.attributes.lat!!.toDouble())
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

