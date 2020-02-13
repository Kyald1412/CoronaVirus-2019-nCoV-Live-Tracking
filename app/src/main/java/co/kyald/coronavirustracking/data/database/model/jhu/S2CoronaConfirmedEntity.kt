package co.kyald.coronavirustracking.data.database.model.jhu
import android.os.Parcelable
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import co.kyald.coronavirustracking.data.database.converter.ListStringTypeConverter
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "corona_data_confirmed_s2")
@Parcelize
data class S2CoronaConfirmedEntity(
    @PrimaryKey
    val id: Int,
    @ColumnInfo(name = "confirmed")
    @TypeConverters(ListStringTypeConverter::class)
    val confirmed:  String
) : Parcelable