package co.kyald.coronavirustracking.data.database.model.worldometers
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "corona_s4_entity")
@Parcelize
data class S4CoronaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    
    @SerializedName("active")
    val active: Long? = 0,
    @SerializedName("cases")
    val cases:  Long? = 0,
    @SerializedName("casesPerOneMillion")
    val casesPerOneMillion: Float? = 0f,
    @SerializedName("country")
    val country: String? = "",
    @Embedded
    @SerializedName("countryInfo")
    val countryInfo: CountryInfo,
    @SerializedName("critical")
    val critical:  Long? = 0,
    @SerializedName("deaths")
    val deaths: Long? = 0,
    @SerializedName("deathsPerOneMillion")
    val deathsPerOneMillion:  Float? = 0f,
    @SerializedName("recovered")
    val recovered:  Long? = 0,
    @SerializedName("todayCases")
    val todayCases:  Long? = 0,
    @SerializedName("todayDeaths")
    val todayDeaths:  Long? = 0,
    @SerializedName("updated")
    val updated: Long? = 0
) : Parcelable {

    @Parcelize
    data class CountryInfo(
        @ColumnInfo(name = "info_flag")
        @SerializedName("flag")
        val info_flag: String? = "",
        @ColumnInfo(name = "info_id")
        @SerializedName("_id")
        val info_id:  Int? = 0,
        @ColumnInfo(name = "info_iso2")
        @SerializedName("iso2")
        val info_iso2: String? = "",
        @ColumnInfo(name = "info_iso3")
        @SerializedName("iso3")
        val info_iso3: String? = "",
        @ColumnInfo(name = "info_lat")
        @SerializedName("lat")
        val info_lat:  Double? = 0.0,
        @ColumnInfo(name = "info_long")
        @SerializedName("long")
        val info_long:  Double? = 0.0
    ) : Parcelable
}
