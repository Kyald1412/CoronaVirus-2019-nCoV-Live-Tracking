package co.kyald.coronavirustracking.data.database.model

data class CoronaEntity(
    val data_source: String = "",
    val data_source_name: String = "",
    val info: DataInfo
) {
    data class DataInfo(
        val country: String? = "",
        val case_actives: Long? = 0,
        val case_confirms: Long? = 0,
        val case_deaths: Long? = 0,
        val case_recovered: Long? = 0,
        val flags: String? = "",
        var longitude: Double? = 0.0,
        val latitude: Double? = 0.0
    )
}
