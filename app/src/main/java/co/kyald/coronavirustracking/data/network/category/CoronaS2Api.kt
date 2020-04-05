package co.kyald.coronavirustracking.data.network.category

import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaEntity
import co.kyald.coronavirustracking.data.database.model.worldometers.S4CoronaEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.ResponseBody
import retrofit2.http.Headers


interface CoronaS2Api {

    @GET("v2/jhucsse")
    suspend fun fetchJHUCSSE(): Response<List<S2CoronaEntity>>

    @GET("countries")
    suspend fun fetchWorlOdMeters(): Response<List<S4CoronaEntity>>

}


