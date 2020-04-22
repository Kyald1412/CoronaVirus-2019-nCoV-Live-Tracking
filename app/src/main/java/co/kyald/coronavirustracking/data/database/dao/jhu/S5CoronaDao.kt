package co.kyald.coronavirustracking.data.database.dao.jhu

import androidx.room.Dao
import androidx.room.Query
import co.kyald.coronavirustracking.data.database.dao.BaseDao
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaEntity
import co.kyald.coronavirustracking.data.database.model.jhu.S5CoronaEntity

@Dao
interface S5CoronaDao : BaseDao<S5CoronaEntity> {

    @Query("SELECT * FROM corona_s5_entity")
    fun getAllCases(): List<S5CoronaEntity>


    @Query("DELETE FROM corona_s5_entity")
    fun deleteAll()
}