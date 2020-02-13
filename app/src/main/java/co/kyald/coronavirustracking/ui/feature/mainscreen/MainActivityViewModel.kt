package co.kyald.coronavirustracking.ui.feature.mainscreen

import android.content.SharedPreferences
import androidx.lifecycle.*
import co.kyald.coronavirustracking.data.database.model.chnasia.S1CoronaEntity
import co.kyald.coronavirustracking.data.repository.CoronaS1Repository
import co.kyald.coronavirustracking.data.repository.CoronaS2Repository
import co.kyald.coronavirustracking.utils.Constants
import com.mapbox.geojson.Feature
import kotlinx.coroutines.*


class MainActivityViewModel(
    private val coronaS1Repository: CoronaS1Repository,
    private val coronaS2Repository: CoronaS2Repository,
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

    val coronaS2ConfirmLiveData: MutableLiveData<List<List<String>>> = coronaS2Repository.coronaConfirmRawLiveData
    val coronaS2DeathLiveData: MutableLiveData<List<List<String>>> = coronaS2Repository.coronaDeathRawLiveData
    val coronaS2RecoverLiveData: MutableLiveData<List<List<String>>> = coronaS2Repository.coronaRecoverRawLiveData

    val coronaS1LiveData: MutableLiveData<S1CoronaEntity> = coronaS1Repository.s1CoronaData

    init {
        refreshData()
    }

    fun refreshData() {
        if (preferences.getString(
                Constants.PREF_DATA_SOURCE,
                Constants.DATA_SOURCE.DATA_S2.value
            ) == Constants.DATA_SOURCE.DATA_S1.value
        ) coronaDataSourceS1() else coronaDataSourceS2()
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

        coronaData = coronaS2Repository.coronaConfirmLiveData
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

}