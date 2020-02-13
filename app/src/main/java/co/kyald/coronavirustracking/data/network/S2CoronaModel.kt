package co.kyald.coronavirustracking.data.network
import com.google.gson.annotations.SerializedName


data class S2CoronaModel(
    @SerializedName("majorDimension")
    val majorDimension: String,
    @SerializedName("range")
    val range: String,
    @SerializedName("values")
    val values: List<List<String>>
)