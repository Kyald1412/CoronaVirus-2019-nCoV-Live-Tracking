package co.kyald.coronavirustracking.data.database.model.jhu
import android.os.Parcelable
import androidx.room.*
import co.kyald.coronavirustracking.data.database.converter.ListStringTypeConverter
import kotlinx.android.parcel.Parcelize
import com.google.gson.annotations.SerializedName


@Entity(tableName = "corona_s2_entity")
@Parcelize
data class S2CoronaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @Embedded
    @SerializedName("coordinates")
    val coordinates: Coordinates,
    @SerializedName("country")
    val country: String? = "",
    @SerializedName("province")
    val province: String? = "",
    @Embedded
    @SerializedName("stats")
    val stats: Stats,
    @SerializedName("updatedAt")
    val updatedAt: String? = ""
) : Parcelable {


    @Parcelize
    data class Coordinates(
        @ColumnInfo(name = "coordinates_latitude")
        @SerializedName("latitude")
        val latitude: String? = "",
        @ColumnInfo(name = "coordinates_longitude")
        @SerializedName("longitude")
        val longitude: String? = ""
    ) : Parcelable

    @Parcelize
    data class Stats(
        @ColumnInfo(name = "stats_confirmed")
        @SerializedName("confirmed")
        val confirmed: Int? = 0,
        @ColumnInfo(name = "stats_deaths")
        @SerializedName("deaths")
        val deaths: Int? = 0,
        @ColumnInfo(name = "stats_recovered")
        @SerializedName("recovered")
        val recovered: Int? = 0
    ) : Parcelable
}

