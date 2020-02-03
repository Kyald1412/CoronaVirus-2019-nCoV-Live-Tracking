package co.kyald.coronavirustracking.data.network.category

import co.kyald.coronavirustracking.data.model.CoronaEntity
import co.kyald.coronavirustracking.utils.Constants
import retrofit2.Response
import retrofit2.http.GET

interface CoronaApi {
    @GET("values?alt=json&key=${Constants.CORONA_API_KEY}")
    suspend fun fetchAll(): Response<CoronaEntity>
}