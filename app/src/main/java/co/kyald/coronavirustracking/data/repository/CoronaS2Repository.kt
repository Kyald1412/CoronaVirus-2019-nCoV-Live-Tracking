package co.kyald.coronavirustracking.data.repository

import androidx.lifecycle.MutableLiveData
import co.kyald.coronavirustracking.data.database.dao.jhu.S2CoronaDao
import co.kyald.coronavirustracking.data.database.model.chnasia.S1CoronaEntity
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaConfirmedEntity
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaDeathsEntity
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaRecoveredEntity
import co.kyald.coronavirustracking.data.network.S2CoronaModel
import co.kyald.coronavirustracking.data.network.category.CoronaS2Api
import co.kyald.coronavirustracking.utils.Constants
import co.kyald.coronavirustracking.utils.InternetChecker
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
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

    val coronaConfirmLiveData: MutableLiveData<List<Feature>> = MutableLiveData()
    val coronaDeathLiveData: MutableLiveData<List<Feature>> = MutableLiveData()
    val coronaRecoveredLiveData: MutableLiveData<List<Feature>> = MutableLiveData()

    var coronaConfirmRawLiveData: MutableLiveData<List<List<String>>> = MutableLiveData()
    var coronaDeathRawLiveData: MutableLiveData<List<List<String>>> = MutableLiveData()
    var coronaRecoverRawLiveData: MutableLiveData<List<List<String>>> = MutableLiveData()


    var isFinished: MutableLiveData<Map<String, Boolean>> = MutableLiveData()

    //Dao
    fun getAllConfirmedCase(): S2CoronaConfirmedEntity = s2CoronaDao.getAllConfirmed()

    fun getAllDeathCase(): S2CoronaDeathsEntity = s2CoronaDao.getAllDeaths()
    fun getAllRecoveredCase(): S2CoronaRecoveredEntity = s2CoronaDao.getAllDataRecovered()

    var confirmCase: MutableLiveData<String> = MutableLiveData()
    var deathCase: MutableLiveData<String> = MutableLiveData()
    var recoverCase: MutableLiveData<String> = MutableLiveData()

    private suspend fun saveConfirmedCase(entities: S2CoronaConfirmedEntity) =
        s2CoronaDao.saveConfirmed(entities)

    private suspend fun saveDeathCase(entities: S2CoronaDeathsEntity) =
        s2CoronaDao.saveDeaths(entities)

    private suspend fun saveRecoveredCase(entities: S2CoronaRecoveredEntity) =
        s2CoronaDao.saveRecovered(entities)


    suspend fun callApiConfirmedCase(): S2CoronaModel? {

        val response = coronaS2Service.fetchConfirmed(Constants.S2_CORONA_API_KEY)

        if (response.isSuccessful) {
            response.body()?.let {
                return it
            }
        }

        return null
    }

    suspend fun callApiDeathCase(): S2CoronaModel? {

        val response = coronaS2Service.fetchDeaths(Constants.S2_CORONA_API_KEY)

        if (response.isSuccessful) {
            response.body()?.let {
                return it
            }
        }

        return null
    }

    suspend fun callApiRecoveredCase(): S2CoronaModel? {

        val response = coronaS2Service.fetchRecovered(Constants.S2_CORONA_API_KEY)

        if (response.isSuccessful) {
            response.body()?.let {
                return it
            }
        }

        return null
    }

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

                        val confirmedCase = callApiConfirmedCase()
                        confirmedCase?.let { it ->

                            coronaConfirmLiveData.postValue(buildDataConfirmed(it.values))
                            coronaConfirmRawLiveData.postValue(it.values)

                            saveConfirmedCase(S2CoronaConfirmedEntity(0, Gson().toJson(it.values)))

                        }

                        val deathCase = callApiDeathCase()
                        deathCase?.let {

                            coronaDeathRawLiveData.postValue(it.values)

                            saveDeathCase(S2CoronaDeathsEntity(0, Gson().toJson(it.values)))
                            buildDataDeath(it.values)

                        }

                        val recoveredCase = callApiRecoveredCase()
                        recoveredCase?.let {

                            coronaRecoverRawLiveData.postValue(it.values)

                            saveRecoveredCase(S2CoronaRecoveredEntity(0, Gson().toJson(it.values)))
                            buildDataRecovered(it.values)
                        }


                    } else {

                        val dataConfirm: List<List<String>> = Gson().fromJson(
                            getAllConfirmedCase().confirmed,
                            object : TypeToken<List<List<String>>>() {}.type
                        )

                        val dataDeath: List<List<String>> = Gson().fromJson(
                            getAllDeathCase().deaths,
                            object : TypeToken<List<List<String>>>() {}.type
                        )

                        val dataRecover: List<List<String>> = Gson().fromJson(
                            getAllRecoveredCase().recovered,
                            object : TypeToken<List<List<String>>>() {}.type
                        )

                        coronaConfirmLiveData.postValue(buildDataConfirmed(dataConfirm))
                        buildDataDeath(dataDeath)
                        buildDataRecovered(dataRecover)

                        coronaConfirmRawLiveData.postValue(dataConfirm)
                        coronaDeathRawLiveData.postValue(dataDeath)
                        coronaRecoverRawLiveData.postValue(dataRecover)

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

    private suspend fun buildDataDeath(dataDeath: List<List<String>>) {
        var deathCount = 0

        // limits the scope of concurrency
        withContext(Dispatchers.IO) {

            dataDeath.filterIndexed { index, _ -> (index != 0) }.forEach { value ->

                deathCount += try {
                    value[value.size - 1].toInt()
                } catch (nfe: NumberFormatException) {
                    1
                }

            }

            deathCase.postValue(deathCount.toString())

        }

    }


    private suspend fun buildDataRecovered(dataRecover: List<List<String>>) {
        var recoverCount = 0

        // limits the scope of concurrency
        withContext(Dispatchers.IO) {

            dataRecover.filterIndexed { index, _ -> (index != 0) }.forEach { value ->

                recoverCount += try {
                    value[value.size - 1].toInt()
                } catch (nfe: NumberFormatException) {
                    1
                }

            }

            recoverCase.postValue(recoverCount.toString())

        }

    }

    private suspend fun buildDataConfirmed(dataConfirmed: List<List<String>>): MutableList<Feature> {
        val featureList: MutableList<Feature> = mutableListOf()
        var confirmedCount = 0

        // limits the scope of concurrency
        withContext(Dispatchers.IO) {

            dataConfirmed.filterIndexed { index, _ -> (index != 0) }.forEach { value ->

                try {
                    confirmedCount += value[value.size - 1].toInt()
                    (0 until value[value.size - 1].toInt()).map {
                        featureList.add(
                            Feature.fromGeometry(
                                Point.fromLngLat(value[3].toDouble(), value[2].toDouble())
                            )
                        )
                    }
                } catch (nfe: NumberFormatException) {
                    confirmedCount += 1
                    featureList.add(
                        Feature.fromGeometry(
                            Point.fromLngLat(value[3].toDouble(), value[2].toDouble())
                        )
                    )
                }

            }

            isFinished.postValue(
                mapOf(
                    "done" to true,
                    "internet" to true
                )
            )
            confirmCase.postValue(confirmedCount.toString())

        }

        return featureList

    }
}

