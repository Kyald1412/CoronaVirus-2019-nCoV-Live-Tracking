package co.kyald.coronavirustracking.data.database.model.jhu
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Entity(tableName = "corona_s5_entity")
@Parcelize
data class S5CoronaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    
    @SerializedName("active")
    val active: Int? = 0,
    @Embedded
    @SerializedName("confirmed")
    val confirmed: Confirmed,
    @Embedded
    @SerializedName("deaths")
    val deaths: Deaths,
    @SerializedName("deltaConfirmed")
    val deltaConfirmed: Int? = 0,
    @Embedded
    @SerializedName("deltaConfirmedDetail")
    val deltaConfirmedDetail: DeltaConfirmedDetail,
    @SerializedName("deltaRecovered")
    val deltaRecovered: Int? = 0,
    @SerializedName("incidentRate")
    val incidentRate: Int? = 0,
    @SerializedName("mainlandChina")
    val mainlandChina: Int? = 0,
    @SerializedName("otherLocations")
    val otherLocations: Int? = 0,
    @SerializedName("peopleTested")
    val peopleTested: Int? = 0,
    @Embedded
    @SerializedName("recovered")
    val recovered: Recovered,
    @SerializedName("reportDate")
    val reportDate: String? = "",
    @SerializedName("totalConfirmed")
    val totalConfirmed: Int? = 0,
    @SerializedName("totalRecovered")
    val totalRecovered: Int? = 0
) : Parcelable {

    @Parcelize
    data class Confirmed(
        @ColumnInfo(name = "confirm_china")
        @SerializedName("china")
        val china: Int? = 0,
        @ColumnInfo(name = "confirm_coutside_china")
        @SerializedName("outsideChina")
        val outsideChina: Int? = 0,
        @ColumnInfo(name = "confirm_total")
        @SerializedName("total")
        val total: Int? = 0
    ) : Parcelable

    @Parcelize
    data class Deaths(
        @ColumnInfo(name = "death_china")
        @SerializedName("china")
        val china: Int? = 0,
        @ColumnInfo(name = "death_coutside_china")
        @SerializedName("outsideChina")
        val outsideChina: Int? = 0,
        @ColumnInfo(name = "death_total")
        @SerializedName("total")
        val total: Int? = 0
    ) : Parcelable

    @Parcelize
    data class DeltaConfirmedDetail(
        @ColumnInfo(name = "deltaconfirm_china")
        @SerializedName("china")
        val china: Int? = 0,
        @ColumnInfo(name = "deltaconfirm_coutside_china")
        @SerializedName("outsideChina")
        val outsideChina: Int? = 0,
        @ColumnInfo(name = "deltaconfirm_total")
        @SerializedName("total")
        val total: Int? = 0
    ) : Parcelable

    @Parcelize
    data class Recovered(
        @ColumnInfo(name = "recovered_china")
        @SerializedName("china")
        val china: Int? = 0,
        @ColumnInfo(name = "recovered_coutside_china")
        @SerializedName("outsideChina")
        val outsideChina: Int? = 0,
        @ColumnInfo(name = "recovered_total")
        @SerializedName("total")
        val total: Int? = 0
    ) : Parcelable
}
