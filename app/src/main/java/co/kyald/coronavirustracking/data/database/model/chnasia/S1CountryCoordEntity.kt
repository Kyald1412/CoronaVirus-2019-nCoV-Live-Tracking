package co.kyald.coronavirustracking.data.database.model.chnasia

import android.os.Parcelable
import androidx.room.*
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "countrycoord")
@Parcelize
data class S1CountryCoordEntity(
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