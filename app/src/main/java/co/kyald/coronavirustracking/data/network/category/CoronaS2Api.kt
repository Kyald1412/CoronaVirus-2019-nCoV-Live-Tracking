package co.kyald.coronavirustracking.data.network.category

import co.kyald.coronavirustracking.BuildConfig.S1_CORONA_API_KEY
import co.kyald.coronavirustracking.data.database.model.chnasia.S1CoronaEntity
import co.kyald.coronavirustracking.data.network.S2CoronaModel
import co.kyald.coronavirustracking.utils.Constants
import com.google.gson.JsonArray
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Query
import java.util.*
import kotlin.collections.ArrayList
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Headers


interface CoronaS2Api {

    //    @GET("Recovered")
//    suspend fun fetchRecovered(@Query("key") api_key: String): Response<S2CoronaModel>
//
//    @GET("Death")
//    suspend fun fetchDeaths(@Query("key") api_key: String): Response<S2CoronaModel>
//
//    @GET("Confirmed")
//    suspend fun fetchConfirmed(@Query("key") api_key: String): Response<S2CoronaModel>
//
//
    @Headers("Accept:text/plain")
    @GET("time_series_19-covid-Recovered.csv")
    suspend fun fetchRecovered(@Query("key") api_key: String): Response<ResponseBody>

    @Headers("Accept:text/plain")
    @GET("time_series_19-covid-Deaths.csv")
    suspend fun fetchDeaths(@Query("key") api_key: String): Response<ResponseBody>

    @Headers("Accept:text/plain")
    @GET("time_series_19-covid-Confirmed.csv")
    suspend fun fetchConfirmed(@Query("key") api_key: String): Response<ResponseBody>
}


