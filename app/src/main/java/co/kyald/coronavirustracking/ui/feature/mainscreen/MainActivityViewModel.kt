package co.kyald.coronavirustracking.ui.feature.mainscreen

import android.content.SharedPreferences
import androidx.lifecycle.*
import co.kyald.coronavirustracking.data.database.model.arcgis.S3CoronaEntity
import co.kyald.coronavirustracking.data.database.model.arcgis.S3CoronaEntityResponse
import co.kyald.coronavirustracking.data.database.model.chnasia.S1CoronaEntity
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaEntity
import co.kyald.coronavirustracking.data.database.model.worldometers.S4CoronaEntity
import co.kyald.coronavirustracking.data.repository.CoronaS1Repository
import co.kyald.coronavirustracking.data.repository.CoronaS2Repository
import co.kyald.coronavirustracking.data.repository.CoronaS3Repository
import co.kyald.coronavirustracking.data.repository.CoronaS4Repository
import co.kyald.coronavirustracking.utils.Constants
import com.mapbox.geojson.Feature
import kotlinx.coroutines.*


class MainActivityViewModel(
    private val coronaS1Repository: CoronaS1Repository,
    private val coronaS2Repository: CoronaS2Repository,
    private val coronaS3Repository: CoronaS3Repository,
    private val coronaS4Repository: CoronaS4Repository,
    private val preferences: SharedPreferences
) : ViewModel() {

    var coronaLiveData = MediatorLiveData<List<Feature>>()
    private var coronaData: MutableLiveData<List<Feature>> = MutableLiveData()

    var isFinishedLiveData = MediatorLiveData<Map<String, Boolean>>()
    private var isFinished: MutableLiveData<Map<String, Boolean>> = MutableLiveData()

    var confirmedCaseLiveData = MediatorLiveData<String>()
    var confirmedDeathLiveData = MediatorLiveData<String>()
    var confirmedRecoveredLiveData = MediatorLiveData<String>()

    private var confirmCase: MutableLiveData<String> = MutableLiveData()
    private var confirmDeath: MutableLiveData<String> = MutableLiveData()
    private var confirmRecover: MutableLiveData<String> = MutableLiveData()

    var currentDataSource: MutableLiveData<String> = MutableLiveData()

    val coronaS1LiveData: MutableLiveData<S1CoronaEntity> = coronaS1Repository.s1CoronaData
    val coronaS2LiveData: MutableLiveData<List<S2CoronaEntity>> = coronaS2Repository.coronaLiveDataS2
    val coronaS3LiveData: MutableLiveData<List<S3CoronaEntity>> = coronaS3Repository.coronaLiveDataS3
    val coronaS4LiveData: MutableLiveData<List<S4CoronaEntity>> = coronaS4Repository.coronaLiveDataS4

    init {
        refreshData()
    }

    fun refreshData() {
        when(preferences.getString(
            Constants.PREF_DATA_SOURCE,
            Constants.DATA_SOURCE.DATA_S4.value
        )){
            Constants.DATA_SOURCE.DATA_S1.value -> coronaDataSourceS1()
            Constants.DATA_SOURCE.DATA_S2.value -> coronaDataSourceS2()
            Constants.DATA_SOURCE.DATA_S3.value -> coronaDataSourceS3()
            Constants.DATA_SOURCE.DATA_S4.value -> coronaDataSourceS4()
        }
    }

    fun coronaDataSourceS1() {
        currentDataSource.postValue(Constants.DATA_SOURCE.DATA_S1.value)

        coronaLiveData.removeSource(coronaData)
        isFinishedLiveData.removeSource(isFinished)

        confirmedCaseLiveData.removeSource(confirmCase)
        confirmedDeathLiveData.removeSource(confirmDeath)
        confirmedRecoveredLiveData.removeSource(confirmRecover)

        coronaS1Repository.fetchCoronaDataS1(Dispatchers.IO, true)

        coronaData = coronaS1Repository.coronaLiveData
        isFinished = coronaS1Repository.isFinished
        confirmCase = coronaS1Repository.confirmCase
        confirmDeath = coronaS1Repository.deathCase

        coronaLiveData.addSource(coronaData) { coronaLiveData.value = it }
        isFinishedLiveData.addSource(isFinished) { isFinishedLiveData.value = it }

        confirmedCaseLiveData.addSource(confirmCase) { confirmedCaseLiveData.value = it }
        confirmedDeathLiveData.addSource(confirmDeath) { confirmedDeathLiveData.value = it }
        confirmedRecoveredLiveData.addSource(confirmRecover) {
            confirmedRecoveredLiveData.value = it
        }
    }

    fun coronaDataSourceS2() {
        currentDataSource.postValue(Constants.DATA_SOURCE.DATA_S2.value)

        coronaLiveData.removeSource(coronaData)
        isFinishedLiveData.removeSource(isFinished)

        confirmedCaseLiveData.removeSource(confirmCase)
        confirmedDeathLiveData.removeSource(confirmDeath)
        confirmedRecoveredLiveData.removeSource(confirmRecover)

        coronaS2Repository.fetchCoronaDataS2()

        coronaData = coronaS2Repository.coronaLiveMapDataS2
        isFinished = coronaS2Repository.isFinished
        confirmCase = coronaS2Repository.confirmCase
        confirmDeath = coronaS2Repository.deathCase
        confirmRecover = coronaS2Repository.recoverCase

        coronaLiveData.addSource(coronaData) { coronaLiveData.value = it }
        isFinishedLiveData.addSource(isFinished) { isFinishedLiveData.value = it }

        confirmedCaseLiveData.addSource(confirmCase) { confirmedCaseLiveData.value = it }
        confirmedDeathLiveData.addSource(confirmDeath) { confirmedDeathLiveData.value = it }
        confirmedRecoveredLiveData.addSource(confirmRecover) {
            confirmedRecoveredLiveData.value = it
        }
    }

    fun coronaDataSourceS3() {
        currentDataSource.postValue(Constants.DATA_SOURCE.DATA_S3.value)

        coronaLiveData.removeSource(coronaData)
        isFinishedLiveData.removeSource(isFinished)

        confirmedCaseLiveData.removeSource(confirmCase)
        confirmedDeathLiveData.removeSource(confirmDeath)
        confirmedRecoveredLiveData.removeSource(confirmRecover)

        coronaS3Repository.fetchCoronaDataS3()

        coronaData = coronaS3Repository.coronaLiveMapDataS3
        isFinished = coronaS3Repository.isFinished
        confirmCase = coronaS3Repository.confirmCase
        confirmDeath = coronaS3Repository.deathCase
        confirmRecover = coronaS3Repository.recoverCase

        coronaLiveData.addSource(coronaData) { coronaLiveData.value = it }
        isFinishedLiveData.addSource(isFinished) { isFinishedLiveData.value = it }

        confirmedCaseLiveData.addSource(confirmCase) { confirmedCaseLiveData.value = it }
        confirmedDeathLiveData.addSource(confirmDeath) { confirmedDeathLiveData.value = it }
        confirmedRecoveredLiveData.addSource(confirmRecover) {
            confirmedRecoveredLiveData.value = it
        }
    }


    fun coronaDataSourceS4() {
        currentDataSource.postValue(Constants.DATA_SOURCE.DATA_S4.value)

        coronaLiveData.removeSource(coronaData)
        isFinishedLiveData.removeSource(isFinished)

        confirmedCaseLiveData.removeSource(confirmCase)
        confirmedDeathLiveData.removeSource(confirmDeath)
        confirmedRecoveredLiveData.removeSource(confirmRecover)

        coronaS4Repository.fetchCoronaDataS4()

        coronaData = coronaS4Repository.coronaLiveMapDataS4
        isFinished = coronaS4Repository.isFinished
        confirmCase = coronaS4Repository.confirmCase
        confirmDeath = coronaS4Repository.deathCase
        confirmRecover = coronaS4Repository.recoverCase

        coronaLiveData.addSource(coronaData) { coronaLiveData.value = it }
        isFinishedLiveData.addSource(isFinished) { isFinishedLiveData.value = it }

        confirmedCaseLiveData.addSource(confirmCase) { confirmedCaseLiveData.value = it }
        confirmedDeathLiveData.addSource(confirmDeath) { confirmedDeathLiveData.value = it }
        confirmedRecoveredLiveData.addSource(confirmRecover) {
            confirmedRecoveredLiveData.value = it
        }
    }

}