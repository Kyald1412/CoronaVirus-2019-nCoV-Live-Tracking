package co.kyald.coronavirustracking.data.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import com.mapbox.geojson.Feature
import com.mapbox.geojson.FeatureCollection
import com.mapbox.geojson.LineString
import com.mapbox.geojson.Point
import com.mapbox.mapboxsdk.geometry.LatLng
import kotlinx.android.parcel.Parcelize


@Parcelize
data class GeoGsonModel(
    @SerializedName("geometry")
    val geometry: Geometry,
    @SerializedName("properties")
    val properties: Properties,
    @SerializedName("type")
    val type: String
) : Parcelable {
    override fun toString(): String {
        return "ASDSAD(geometry=$geometry, properties=$properties, type='$type')"
    }
}

@Parcelize
data class Geometry(
    @SerializedName("coordinates")
    val coordinates: List<Double>,
    @SerializedName("type")
    val type: String
) : Parcelable {

    override fun toString(): String {
        return "Geometry(coordinates=$coordinates, type='$type')"
    }
}

@Parcelize
class Properties : Parcelable {
    override fun toString(): String {
        return "Properties()"
    }
}