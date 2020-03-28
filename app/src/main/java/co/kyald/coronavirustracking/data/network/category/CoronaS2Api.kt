package co.kyald.coronavirustracking.data.network.category

import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query
import okhttp3.ResponseBody
import retrofit2.http.Headers


interface CoronaS2Api {

    @GET("jhucsse")
    suspend fun fetchJHUCSSE(): Response<List<S2CoronaEntity>>

}


