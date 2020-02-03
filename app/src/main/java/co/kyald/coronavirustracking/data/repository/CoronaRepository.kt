package co.kyald.coronavirustracking.data.repository

import co.kyald.coronavirustracking.data.model.CoronaEntity
import co.kyald.coronavirustracking.data.database.dao.CoronaDao
import co.kyald.coronavirustracking.data.database.dao.CountryCoordDao
import co.kyald.coronavirustracking.data.model.CountryCoordEntity
import co.kyald.coronavirustracking.data.network.category.CoronaApi
import com.google.gson.JsonArray
import timber.log.Timber

class CoronaRepository(
    private val coronaDao: CoronaDao,
    private val countryCoordDao: CountryCoordDao,
    private val coronaService: CoronaApi
) {

    fun getAll(): CoronaEntity = coronaDao.getAll()
    fun getJsonEntry(): String = coronaDao.getJsonEntry()
    fun getCountryCoord(): List<CountryCoordEntity> = countryCoordDao.getCountryCoord()

    suspend fun saveCoronaData(entities: CoronaEntity) = coronaDao.save(entities)

    suspend fun saveCountryCoord(coordJson: CountryCoordEntity) = countryCoordDao.save(coordJson)
    suspend fun deleteCountryCoord() = countryCoordDao.deleteAllCountryCoord()

    suspend fun fetchAll(): CoronaEntity? {

        val response = coronaService.fetchAll()

        if (response.isSuccessful) {
            response.body()?.let {
                return it
            }
        }

        return null
    }

}
