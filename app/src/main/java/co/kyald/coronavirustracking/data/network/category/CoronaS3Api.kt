package co.kyald.coronavirustracking.data.network.category

import co.kyald.coronavirustracking.data.database.model.arcgis.S3CoronaEntityResponse
import co.kyald.coronavirustracking.data.database.model.arcgis.S3CoronaEntityResponseCases
import retrofit2.Response
import retrofit2.http.GET


interface CoronaS3Api {

    @GET("query?f=json&where=(Confirmed%3E0)%20OR%20(Deaths%3E0)%20OR%20(Recovered%3E0)&returnGeometry=false&spatialRef=esriSpatialRelIntersects&outFields=*&orderByFields=Country_Region%20asc,Province_State%20asc&resultOffset=0&resultRecordCount=250&cacheHint=false")
    suspend fun fetchArcGIS(): Response<S3CoronaEntityResponse>

    @GET("query?f=json&where=Confirmed%20>%200&returnGeometry=false&spatialRel=esriSpatialRelIntersects&outFields=*&outStatistics=%5B%7B\"statisticType\"%3A\"sum\",\"onStatisticField\"%3A\"Confirmed\",\"outStatisticFieldName\"%3A\"value\"%7D%5D&cacheHint=false")
    suspend fun fetchArcGISConfirmed(): Response<S3CoronaEntityResponseCases>

    @GET("query?f=json&where=Confirmed%20>%200&returnGeometry=false&spatialRel=esriSpatialRelIntersects&outFields=*&outStatistics=%5B%7B\"statisticType\"%3A\"sum\",\"onStatisticField\"%3A\"Recovered\",\"outStatisticFieldName\"%3A\"value\"%7D%5D&cacheHint=false")
    suspend fun fetchArcGISRecovered(): Response<S3CoronaEntityResponseCases>

    @GET("query?f=json&where=Confirmed%20>%200&returnGeometry=false&spatialRel=esriSpatialRelIntersects&outFields=*&outStatistics=%5B%7B\"statisticType\":\"sum\",\"onStatisticField\":\"Deaths\",\"outStatisticFieldName\":\"value\"%7D%5D&cacheHint=false")
    suspend fun fetchArcGISDeaths(): Response<S3CoronaEntityResponseCases>

}


