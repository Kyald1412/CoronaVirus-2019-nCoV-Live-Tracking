package co.kyald.coronavirustracking.data.network.category

import co.kyald.coronavirustracking.data.model.CoronaEntity
import retrofit2.Response
import retrofit2.http.GET

interface CoronaApi {
    @GET("values?alt=json&key=AIzaSyDnpMUhVLaY-hv7V_DxUp8oePTBpI9JBmQ")
    suspend fun fetchAll(): Response<CoronaEntity>
}