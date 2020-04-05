package co.kyald.coronavirustracking.data.database.dao.worldometers

import androidx.room.Dao
import androidx.room.Query
import co.kyald.coronavirustracking.data.database.dao.BaseDao
import co.kyald.coronavirustracking.data.database.model.worldometers.S4CoronaEntity

@Dao
interface S4CoronaDao : BaseDao<S4CoronaEntity> {

    @Query("SELECT * FROM corona_s4_entity")
    fun getAllCases(): List<S4CoronaEntity>

    @Query("SELECT SUM(cases) FROM corona_s4_entity")
    fun getTotalConfirmedCase(): Int

    @Query("SELECT SUM(deaths) FROM corona_s4_entity")
    fun getTotalDeathCase(): Int

    @Query("SELECT SUM(recovered) FROM corona_s4_entity")
    fun getTotalRecoveredCase(): Int

    @Query("DELETE FROM corona_s4_entity")
    fun deleteAll()
}