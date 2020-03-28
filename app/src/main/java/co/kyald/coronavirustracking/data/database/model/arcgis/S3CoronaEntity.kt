package co.kyald.coronavirustracking.data.database.model.arcgis

import android.os.Parcelable
import androidx.room.*
import co.kyald.coronavirustracking.data.database.converter.ListStringTypeConverter
import kotlinx.android.parcel.Parcelize
import com.google.gson.annotations.SerializedName


@Parcelize
data class S3CoronaEntityResponse(
    @SerializedName("features")
    var features: List<S3CoronaEntity>
) : Parcelable

@Parcelize
data class S3CoronaEntityResponseCases(
    @SerializedName("features")
    var features: List<S3CoronaEntityAttributes>
) : Parcelable {
    @Parcelize
    data class S3CoronaEntityAttributes(
        @SerializedName("attributes")
        var attributes: CasesAttributes
    ) : Parcelable {
        @Parcelize
        data class CasesAttributes(
            @SerializedName("value")
            var value: Int
        ) : Parcelable
    }
}

@Entity(tableName = "corona_s3_entity")
@Parcelize
data class S3CoronaEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @Embedded
    @SerializedName("attributes")
    var attributes: Attributes
) : Parcelable

@Parcelize
data class Attributes(
    @ColumnInfo(name = "attr_active")
    @SerializedName("Active")
    var active: Int? = 0,
    @ColumnInfo(name = "attr_combined_key")
    @SerializedName("Combined_Key")
    var combinedKey: String? = "",
    @ColumnInfo(name = "attr_confirmed")
    @SerializedName("Confirmed")
    var confirmed: Int? = 0,
    @ColumnInfo(name = "attr_country")
    @SerializedName("Country_Region")
    var countryRegion: String? = "",
    @ColumnInfo(name = "attr_deaths")
    @SerializedName("Deaths")
    var deaths: Int? = 0,
    @ColumnInfo(name = "attr_last_update")
    @SerializedName("Last_Update")
    var lastUpdate: Long? = 0,
    @ColumnInfo(name = "attr_lat")
    @SerializedName("Lat")
    var lat: Double? = 0.0,
    @ColumnInfo(name = "attr_long")
    @SerializedName("Long_")
    var long: Double? = 0.0,
    @ColumnInfo(name = "attr_province")
    @SerializedName("Province_State")
    var provinceState: String? = "",
    @ColumnInfo(name = "attr_recovered")
    @SerializedName("Recovered")
    var recovered: Int? = 0
) : Parcelable