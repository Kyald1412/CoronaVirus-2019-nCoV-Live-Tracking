package co.kyald.coronavirustracking.data.database.converter

import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class ListStringTypeConverter {
    companion object {
        private val gson = Gson()

        @TypeConverter
        @JvmStatic
        fun toString(data: String?): List<String> {
            if (data == null) {
                return emptyList()
            }
            val listType = object : TypeToken<List<String>>() {}.type
            return gson.fromJson(data, listType)
        }

        @TypeConverter
        @JvmStatic
        fun fromString(entry: List<String>): String {
            return gson.toJson(entry)
        }
    }
}