package co.kyald.coronavirustracking.data.network.category

import co.kyald.coronavirustracking.data.database.model.chnasia.S1CoronaEntity
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface CoronaS1Api {
    @GET("values?alt=json")
    suspend fun fetchCoronaS1(@Query("key") api_key: String): Response<S1CoronaEntity>
}