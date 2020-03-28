package co.kyald.coronavirustracking.data.database.converter

import androidx.room.TypeConverter

object NullStringTypeConverter {
    @TypeConverter
    fun fromNullToString(value: String?): String {
        return value ?: ""
    }
}