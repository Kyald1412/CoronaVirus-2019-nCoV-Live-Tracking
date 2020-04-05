package co.kyald.coronavirustracking.data.repository

import androidx.lifecycle.MutableLiveData
import co.kyald.coronavirustracking.data.database.dao.worldometers.S4CoronaDao
import co.kyald.coronavirustracking.data.database.model.CoronaEntity
import co.kyald.coronavirustracking.data.database.model.worldometers.S4CoronaEntity
import co.kyald.coronavirustracking.data.network.category.CoronaS2Api
import co.kyald.coronavirustracking.utils.Constants
import co.kyald.coronavirustracking.utils.InternetChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


class CoronaS4Repository(
    private val s4CoronaDao: S4CoronaDao,
    private val coronaS4Service: CoronaS2Api
) {

    var coronaLiveDataS4: MutableLiveData<List<CoronaEntity>> = MutableLiveData()

    var isFinished: MutableLiveData<Map<String, Boolean>> = MutableLiveData()

    var totalCases: MutableLiveData<Map<String, String>> = MutableLiveData()

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
                                buildData(it)
                                s4CoronaDao.save(it)
                            }

                        }

                        totalCases.postValue(
                            mapOf(
                                "confirmed" to s4CoronaDao.getTotalConfirmedCase().toString(),
                                "recovered" to s4CoronaDao.getTotalRecoveredCase().toString(),
                                "deaths" to s4CoronaDao.getTotalDeathCase().toString()
                            )
                        )


                    } else {

                        buildData(s4CoronaDao.getAllCases())

                        totalCases.postValue(
                            mapOf(
                                "confirmed" to s4CoronaDao.getTotalConfirmedCase().toString(),
                                "recovered" to s4CoronaDao.getTotalRecoveredCase().toString(),
                                "deaths" to s4CoronaDao.getTotalDeathCase().toString()
                            )
                        )
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

    private suspend fun buildData(data: List<S4CoronaEntity>) {
        val coronaEntity: MutableList<CoronaEntity> = mutableListOf()

        // limits the scope of concurrency
        withContext(Dispatchers.IO) {

            data.forEach {

                withContext(Dispatchers.IO) {
                    try {
                        coronaEntity.add(
                            CoronaEntity(
                                data_source = Constants.DATA_SOURCE.DATA_S4.value,
                                data_source_name = "worldometers",
                                info = CoronaEntity.DataInfo(
                                    country = it.country,
                                    latitude = it.countryInfo.info_lat ?: 0.0,
                                    longitude = it.countryInfo.info_long ?: 0.0,
                                    case_actives = it.active,
                                    case_confirms = it.cases,
                                    case_deaths = it.deaths,
                                    case_recovered = it.recovered,
                                    flags = it.countryInfo.info_flag
                                )
                            )
                        )
                    } catch (nfe: NumberFormatException) {
                        coronaEntity.add(
                            CoronaEntity(
                                data_source = Constants.DATA_SOURCE.DATA_S4.value,
                                data_source_name = "worldometers",
                                info = CoronaEntity.DataInfo(
                                    country = it.country,
                                    latitude = 0.0,
                                    longitude = 0.0,
                                    case_actives = it.active,
                                    case_confirms = it.cases,
                                    case_deaths = it.deaths,
                                    case_recovered = it.recovered,
                                    flags = it.countryInfo.info_flag
                                )
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
            coronaLiveDataS4.postValue(coronaEntity)
        }

    }

}

