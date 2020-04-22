package co.kyald.coronavirustracking.data.network.category

import co.kyald.coronavirustracking.data.database.model.arcgis.S3CoronaEntityResponse
import co.kyald.coronavirustracking.data.database.model.arcgis.S3CoronaEntityResponseCases
import co.kyald.coronavirustracking.data.database.model.jhu.S5CoronaEntity
import retrofit2.Response
import retrofit2.http.GET


interface CoronaS5Api {

    @GET("daily")
    suspend fun fetchJHUDaily(): Response<List<S5CoronaEntity>>

}


