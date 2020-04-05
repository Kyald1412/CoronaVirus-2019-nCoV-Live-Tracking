package co.kyald.coronavirustracking.data.database.model.arcgis

import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Embedded
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Parcelize
data class S3CoronaEntityResponse(
    @SerializedName("features")
    val features: List<S3CoronaEntity>
) : Parcelable

@Parcelize
data class S3CoronaEntityResponseCases(
    @SerializedName("features")
    val features: List<S3CoronaEntityAttributes>
) : Parcelable {
    @Parcelize
    data class S3CoronaEntityAttributes(
        @SerializedName("attributes")
        val attributes: CasesAttributes
    ) : Parcelable {
        @Parcelize
        data class CasesAttributes(
            @SerializedName("value")
            val value: Int
        ) : Parcelable
    }
}

@Entity(tableName = "corona_s3_entity")
@Parcelize
data class S3CoronaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @Embedded
    @SerializedName("attributes")
    val attributes: Attributes
) : Parcelable

@Parcelize
data class Attributes(
    @ColumnInfo(name = "attr_active")
    @SerializedName("Active")
    val attr_active: Int? = 0,
    @ColumnInfo(name = "attr_combined_key")
    @SerializedName("Combined_Key")
    val attr_combinedKey: String? = "",
    @ColumnInfo(name = "attr_confirmed")
    @SerializedName("Confirmed")
    val attr_confirmed: Int? = 0,
    @ColumnInfo(name = "attr_country")
    @SerializedName("Country_Region")
    val attr_countryRegion: String? = "",
    @ColumnInfo(name = "attr_deaths")
    @SerializedName("Deaths")
    val attr_deaths: Int? = 0,
    @ColumnInfo(name = "attr_last_update")
    @SerializedName("Last_Update")
    val attr_lastUpdate: Long? = 0,
    @ColumnInfo(name = "attr_lat")
    @SerializedName("Lat")
    val attr_lat: Double? = 0.0,
    @ColumnInfo(name = "attr_long")
    @SerializedName("Long_")
    val attr_long: Double? = 0.0,
    @ColumnInfo(name = "attr_province")
    @SerializedName("Province_State")
    val attr_provinceState: String? = "",
    @ColumnInfo(name = "attr_recovered")
    @SerializedName("Recovered")
    val attr_recovered: Int? = 0
) : Parcelable