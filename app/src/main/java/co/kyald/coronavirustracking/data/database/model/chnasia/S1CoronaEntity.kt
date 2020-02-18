package co.kyald.coronavirustracking.data.database.model.chnasia

import android.os.Parcelable
import androidx.room.*
import co.kyald.coronavirustracking.data.database.converter.EntryConverter
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "coronaentity")
@Parcelize
data class S1CoronaEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int,
    @SerializedName("feed")
    @Embedded
    val feed: Feed
) : Parcelable {

    @Parcelize
    data class Feed(
        @SerializedName("entry")
        @ColumnInfo(name = "entry")
        @TypeConverters(EntryConverter::class)
        val entry: List<Entry>,
        @SerializedName("title")
        @Embedded
        val title: TitleX,
        @SerializedName("updated")
        @Embedded
        val updated: UpdatedX
    ) : Parcelable

    @Parcelize
    data class Entry(
        @SerializedName("content")
        @Embedded
        val content: Content,
        @SerializedName("gsx\$confirmedcases")
        @Embedded
        val gsxconfirmedcases: Gsxconfirmedcases,
        @SerializedName("gsx\$country")
        @Embedded
        val gsxcountry: Gsxcountry,
        @SerializedName("gsx\$reporteddeaths")
        @Embedded
        val gsxreporteddeaths: Gsxreporteddeaths,
        @SerializedName("title")
        @Embedded
        val title: Title,
        @SerializedName("updated")
        @Embedded
        val updated: Updated
    ) : Parcelable

    @Parcelize
    data class Content(
        @ColumnInfo(name = "ContentT")
        @SerializedName("\$t")
        val t: String,
        @ColumnInfo(name = "ContentType")
        @SerializedName("type")
        val type: String
    ) : Parcelable

    @Parcelize
    data class Gsxconfirmedcases(
        @ColumnInfo(name = "GsxconfirmedcasesT")
        @SerializedName("\$t")
        val t: String
    ) : Parcelable{

        fun parsedT(): String {
            return t.replace(".","").replace(",","")
        }
    }

    @Parcelize
    data class Gsxcountry(
        @ColumnInfo(name = "GsxcountryT")
        @SerializedName("\$t")
        val t: String
    ) : Parcelable

    @Parcelize
    data class Gsxreporteddeaths(
        @ColumnInfo(name = "GsxreporteddeathsT")
        @SerializedName("\$t")
        var t: String
    ) : Parcelable{
        fun parsedT(): String {
            return t.replace(".","").replace(",","")
        }
    }

    @Parcelize
    data class Title(
        @ColumnInfo(name = "TitleT")
        @SerializedName("\$t")
        val t: String,
        @ColumnInfo(name = "TitleType")
        @SerializedName("type")
        val type: String
    ) : Parcelable

    @Parcelize
    data class Updated(
        @ColumnInfo(name = "UpdatedT")
        @SerializedName("\$t")
        val t: String
    ) : Parcelable

    @Parcelize
    data class TitleX(
        @ColumnInfo(name = "TitleXT")
        @SerializedName("\$t")
        val t: String,
        @ColumnInfo(name = "TitleXType")
        @SerializedName("type")
        val type: String
    ) : Parcelable

    @Parcelize
    data class UpdatedX(
        @ColumnInfo(name = "UpdatedXT")
        @SerializedName("\$t")
        val t: String
    ) : Parcelable
}