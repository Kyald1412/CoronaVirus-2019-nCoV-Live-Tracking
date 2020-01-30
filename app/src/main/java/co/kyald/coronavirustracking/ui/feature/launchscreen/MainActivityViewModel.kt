package co.kyald.coronavirustracking.ui.feature.launchscreen

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import co.kyald.coronavirustracking.data.model.CoronaEntity
import co.kyald.coronavirustracking.data.repository.CoronaRepository
import com.mapbox.api.geocoding.v5.GeocodingCriteria
import com.mapbox.api.geocoding.v5.MapboxGeocoding
import com.mapbox.api.geocoding.v5.models.GeocodingResponse
import com.mapbox.geojson.Feature
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import timber.log.Timber
import kotlin.coroutines.CoroutineContext

class MainActivityViewModel(
    private val coronaRepository: CoronaRepository
) : ViewModel() {

    var access_token : String = ""
    val coronaLiveData: MutableLiveData<CoronaEntity> = MutableLiveData()
    val coronaCountryList: MutableList<String> = mutableListOf()
    val coronaLngLatList: MutableList<Feature> = mutableListOf()
    val coronaLngLatListLiveData: MutableLiveData<MutableList<Feature>> = MutableLiveData()
    var coronaDataIsFinished : MutableLiveData<Boolean> = MutableLiveData()

    fun fetchcoronaData(coroutineContext: CoroutineContext = Dispatchers.IO): LiveData<CoronaEntity> {
        coronaLiveData.value = coronaRepository.getAll().value

        coronaDataIsFinished.postValue(false)

        CoroutineScope(coroutineContext).launch {
            val newData = coronaRepository.fetchAll()

            newData?.let {
                coronaRepository.save(it)
                coronaLiveData.postValue(newData)

                for (i in it.feed.entry.indices){
                    getLngLatFromCountryName(it.feed.entry[i].gsxcountry.t)
                    Timber.e(it.feed.entry[i].gsxcountry.t)
                }
            }
        }

        return coronaLiveData
    }

    fun getLngLatFromCountryName(countryName: String) {
        val client = MapboxGeocoding.builder()
            .accessToken(access_token)
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
                        coronaLngLatList.add(
                            Feature.fromGeometry(
                                feature.geometry()
                            )
                        )
                        coronaCountryList.add(countryName)
                        coronaLngLatListLiveData.postValue(coronaLngLatList)

                        if(coronaLngLatList.size == coronaLiveData.value?.feed?.entry?.size){
                            coronaDataIsFinished.postValue(true)
                        }
                    }
                }
            }
        })
    }

}