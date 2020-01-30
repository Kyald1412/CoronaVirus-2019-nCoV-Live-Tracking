package co.kyald.coronavirustracking.data.database.converter

class ListStringTypeConverter {

//    private var moshi: Moshi = Moshi.Builder().build()
//
//    @TypeConverter
//    fun fromList(list: List<String>?): String? {
//        if (list == null || list.isEmpty()) {
//            return null
//        }
//
//        val parameterizedType =
//            Types.newParameterizedType(List::class.java, String::class.java)
//        val adapter: JsonAdapter<List<String>> = moshi.adapter(parameterizedType)
//
//        return adapter.toJson(list)
//    }
//
//    @TypeConverter
//    fun toList(s: String): List<String>? {
//        if (s == null || s.isEmpty()) {
//            return null
//        }
//
//        val parameterizedType = Types.newParameterizedType(List::class.java, String::class.java)
//        val adapter: JsonAdapter<List<String>> = moshi.adapter(parameterizedType)
//
//        return adapter.fromJson(s)
//    }
//
//
//    @TypeConverter
//    fun fromEntryList(list: List<CoronaEntity.Entry>?): String? {
//        if (list == null || list.isEmpty()) {
//            return null
//        }
//
//        val parameterizedType =
//            Types.newParameterizedType(List::class.java, CoronaEntity.Entry::class.java)
//        val adapter: JsonAdapter<List<CoronaEntity.Entry>> = moshi.adapter(parameterizedType)
//
//        return adapter.toJson(list)
//    }
//
//    @TypeConverter
//    fun toEntryList(s: String): List<CoronaEntity.Entry>? {
//        if (s == null || s.isEmpty()) {
//            return null
//        }
//
//        val parameterizedType = Types.newParameterizedType(List::class.java, CoronaEntity.Entry::class.java)
//        val adapter: JsonAdapter<List<CoronaEntity.Entry>> = moshi.adapter(parameterizedType)
//
//        return adapter.fromJson(s)
//    }
}