package co.kyald.coronavirustracking.data.repository

import androidx.lifecycle.MutableLiveData
import co.kyald.coronavirustracking.data.database.dao.jhu.S5CoronaDao
import co.kyald.coronavirustracking.data.database.model.CoronaEntity
import co.kyald.coronavirustracking.data.database.model.jhu.S5CoronaEntity
import co.kyald.coronavirustracking.data.database.model.worldometers.S4CoronaEntity
import co.kyald.coronavirustracking.data.network.category.CoronaS2Api
import co.kyald.coronavirustracking.data.network.category.CoronaS5Api
import co.kyald.coronavirustracking.utils.Constants
import co.kyald.coronavirustracking.utils.InternetChecker
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.coroutines.CoroutineContext


class CoronaS5Repository(
    private val s5CoronaDao: S5CoronaDao,
    private val coronaS5Service: CoronaS5Api
) {

    var coronaLiveDataS5: MutableLiveData<List<S5CoronaEntity>> = MutableLiveData()

    fun getCoronaDataS5(): List<S5CoronaEntity> = s5CoronaDao.getAllCases()

    suspend fun callCoronaS5Data() = coronaS5Service.fetchJHUDaily()

    fun fetchCoronaDataS5(coroutineContext: CoroutineContext = Dispatchers.IO) {

        InternetChecker(object : InternetChecker.Consumer {
            override fun accept(internet: Boolean) {

                CoroutineScope(coroutineContext).launch {

                    if (internet) {

                        val caseResponse = callCoronaS5Data()

                        if (caseResponse.isSuccessful) {
                            s5CoronaDao.deleteAll()

                            caseResponse.body()?.let { it ->
                                s5CoronaDao.save(it)
                            }

                        }

                    } else {

                    }
                }
            }
        })
    }

}

