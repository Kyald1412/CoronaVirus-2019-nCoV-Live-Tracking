package co.kyald.coronavirustracking.data.database.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Query
import co.kyald.coronavirustracking.data.model.CoronaEntity

@Dao
interface CoronaDao : BaseDao<CoronaEntity> {

    @Query("SELECT * FROM CoronaEntity")
    fun getAll(): LiveData<CoronaEntity>

}