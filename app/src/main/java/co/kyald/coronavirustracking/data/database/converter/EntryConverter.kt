package co.kyald.coronavirustracking.data.database.converter

import androidx.room.TypeConverter
import co.kyald.coronavirustracking.data.model.CoronaEntity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken


class EntryConverter {
    companion object {
        private val gson = Gson()

        @TypeConverter
        @JvmStatic
        fun toEntry(data: String?): List<CoronaEntity.Entry> {
            if (data == null) {
                return emptyList()
            }
            val listType = object : TypeToken<List<CoronaEntity.Entry>>() {}.type
            return gson.fromJson(data, listType)
        }

        @TypeConverter
        @JvmStatic
        fun fromEntry(entry: List<CoronaEntity.Entry>): String {
            return gson.toJson(entry)
        }
    }
}