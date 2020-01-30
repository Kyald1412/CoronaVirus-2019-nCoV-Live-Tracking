package co.kyald.coronavirustracking.data.repository

import androidx.lifecycle.LiveData
import co.kyald.coronavirustracking.data.model.CoronaEntity
import co.kyald.coronavirustracking.data.database.dao.CoronaDao
import co.kyald.coronavirustracking.data.network.category.CoronaApi
import timber.log.Timber

class CoronaRepository(
    private val coronaDao: CoronaDao,
    private val coronaService: CoronaApi
) {

    fun getAll(): LiveData<CoronaEntity> = coronaDao.getAll()

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

    suspend fun save(entities: CoronaEntity) {

        Timber.e("Save data $entities")
        coronaDao.save(entities)
    }
}
