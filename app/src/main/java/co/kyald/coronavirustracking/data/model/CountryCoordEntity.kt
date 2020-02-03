package co.kyald.coronavirustracking.data.model

import android.os.Parcelable
import androidx.room.*
import co.kyald.coronavirustracking.data.database.converter.EntryConverter
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "countrycoord")
@Parcelize
data class CountryCoordEntity(
    @PrimaryKey(autoGenerate = true)
    var id: Int,
    @ColumnInfo(name = "longitude")
    @SerializedName("longitude")
    val longitude: Double,
    @ColumnInfo(name = "latitude")
    @SerializedName("latitude")
    val latitude: Double,
    @ColumnInfo(name = "country")
    @SerializedName("country")
    val country: String
) : Parcelable