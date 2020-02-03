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

    suspend fun fetchAll(): CoronaEntity? {

        val response = coronaService.fetchAll()

        if (response.isSuccessful) {
            response.body()?.let {
                Timber.e("RESPONSE $it")
                return it
            }
        }

        return null
    }

    suspend fun saveCoronaData(entities: CoronaEntity) {

        val addedID = coronaDao.save(entities)
        Timber.e("Inserted ID $addedID")

    }


    suspend fun saveCountryCoord(coordJson: CountryCoordEntity) {

        val addedID = countryCoordDao.save(coordJson)
        Timber.e("Inserted ID $addedID")

    }
    suspend fun deleteCountryCoord() {

        val addedID = countryCoordDao.deleteAllCountryCoord()
        Timber.e("Inserted ID $addedID")

    }

}
