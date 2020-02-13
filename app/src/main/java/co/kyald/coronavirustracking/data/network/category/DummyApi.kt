package co.kyald.coronavirustracking.data.network.category

import co.kyald.coronavirustracking.data.database.model.DummyEntity
import retrofit2.Response
import retrofit2.http.GET

interface DummyApi {
    @GET("v2/5d08c0f2340000f79d5d9a31")
    suspend fun fetchAll(): Response<List<DummyEntity>>
}