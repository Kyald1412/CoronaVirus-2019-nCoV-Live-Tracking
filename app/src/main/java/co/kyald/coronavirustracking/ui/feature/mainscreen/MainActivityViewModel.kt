package co.kyald.coronavirustracking.ui.feature.mainscreen

import android.content.SharedPreferences
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.kyald.coronavirustracking.data.database.model.CoronaEntity
import co.kyald.coronavirustracking.data.repository.CoronaS2Repository
import co.kyald.coronavirustracking.data.repository.CoronaS3Repository
import co.kyald.coronavirustracking.data.repository.CoronaS4Repository
import co.kyald.coronavirustracking.utils.Constants


class MainActivityViewModel(
    private val coronaS2Repository: CoronaS2Repository,
    private val coronaS3Repository: CoronaS3Repository,
    private val coronaS4Repository: CoronaS4Repository,
    private val preferences: SharedPreferences
) : ViewModel() {

    var isFinishedLiveData = MediatorLiveData<Map<String, Boolean>>()
    private var isFinishedSource: MutableLiveData<Map<String, Boolean>> = MutableLiveData()

    var coronaLiveData = MediatorLiveData<List<CoronaEntity>>()
    private var coronaLiveDataSource: MutableLiveData<List<CoronaEntity>> = MutableLiveData()

    var totalCases = MediatorLiveData<Map<String, String>>()
    private var totalCasesSource: MutableLiveData<Map<String, String>> = MutableLiveData()

    private var currentDataSource: MutableLiveData<String> = MutableLiveData()

    init {
        refreshData()
    }

    fun refreshData() {

        isFinishedLiveData.removeSource(isFinishedSource)
        coronaLiveData.removeSource(coronaLiveDataSource)
        totalCases.removeSource(totalCasesSource)

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

    private fun coronaDataSourceS1() {
    }

    private fun coronaDataSourceS2() {
        currentDataSource.postValue(Constants.DATA_SOURCE.DATA_S2.value)

        coronaS2Repository.fetchCoronaDataS2()

        coronaLiveDataSource = coronaS2Repository.coronaLiveDataS2
        totalCasesSource = coronaS2Repository.totalCases
        isFinishedSource = coronaS2Repository.isFinished


        isFinishedLiveData.addSource(isFinishedSource) {
            isFinishedLiveData.value = it
        }
        totalCases.addSource(totalCasesSource) {
            totalCases.value = it
        }
        coronaLiveData.addSource(coronaLiveDataSource) {
            coronaLiveData.value = it
        }
    }

    private fun coronaDataSourceS3() {
        currentDataSource.postValue(Constants.DATA_SOURCE.DATA_S3.value)

        coronaS3Repository.fetchCoronaDataS3()

        coronaLiveDataSource = coronaS3Repository.coronaLiveDataS3
        totalCasesSource = coronaS3Repository.totalCases
        isFinishedSource = coronaS3Repository.isFinished


        isFinishedLiveData.addSource(isFinishedSource) {
            isFinishedLiveData.value = it
        }
        totalCases.addSource(totalCasesSource) {
            totalCases.value = it
        }
        coronaLiveData.addSource(coronaLiveDataSource) {
            coronaLiveData.value = it
        }
    }


    private fun coronaDataSourceS4() {
        currentDataSource.postValue(Constants.DATA_SOURCE.DATA_S4.value)

        coronaS4Repository.fetchCoronaDataS4()

        coronaLiveDataSource = coronaS4Repository.coronaLiveDataS4
        totalCasesSource = coronaS4Repository.totalCases
        isFinishedSource = coronaS4Repository.isFinished


        isFinishedLiveData.addSource(isFinishedSource) {
            isFinishedLiveData.value = it
        }
        totalCases.addSource(totalCasesSource) {
            totalCases.value = it
        }
        coronaLiveData.addSource(coronaLiveDataSource) {
            coronaLiveData.value = it
        }
    }

}