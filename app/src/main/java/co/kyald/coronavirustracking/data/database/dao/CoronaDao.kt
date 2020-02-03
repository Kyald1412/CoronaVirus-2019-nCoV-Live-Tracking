package co.kyald.coronavirustracking.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.kyald.coronavirustracking.data.model.CoronaEntity

@Dao
interface CoronaDao : BaseDao<CoronaEntity> {

    @Query("SELECT * FROM coronaentity")
    fun getAll(): CoronaEntity

    @Query("SELECT entry FROM coronaentity")
    fun getJsonEntry(): String

}