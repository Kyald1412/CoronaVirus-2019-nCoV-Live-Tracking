package co.kyald.coronavirustracking.data.database.dao.jhu

import androidx.room.Dao
import androidx.room.Query
import co.kyald.coronavirustracking.data.database.dao.BaseDao
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaEntity

@Dao
interface S2CoronaDao : BaseDao<S2CoronaEntity> {

    @Query("SELECT * FROM corona_s2_entity")
    fun getAllCases(): List<S2CoronaEntity>

    @Query("SELECT SUM(stats_confirmed) FROM corona_s2_entity")
    fun getTotalConfirmedCase(): Int

    @Query("SELECT SUM(stats_deaths) FROM corona_s2_entity")
    fun getTotalDeathCase(): Int

    @Query("SELECT SUM(stats_recovered) FROM corona_s2_entity")
    fun getTotalRecoveredCase(): Int

    @Query("DELETE FROM corona_s2_entity")
    fun deleteAll()
}