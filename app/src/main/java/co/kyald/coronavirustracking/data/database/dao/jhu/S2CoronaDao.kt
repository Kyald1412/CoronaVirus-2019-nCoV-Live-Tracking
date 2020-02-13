package co.kyald.coronavirustracking.data.database.dao.jhu

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaConfirmedEntity
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaDeathsEntity
import co.kyald.coronavirustracking.data.database.model.jhu.S2CoronaRecoveredEntity

@Dao
interface S2CoronaDao {

    @Query("SELECT * FROM corona_data_recovered_s2")
    fun getAllDataRecovered(): S2CoronaRecoveredEntity

    @Query("SELECT * FROM corona_data_confirmed_s2")
    fun getAllConfirmed(): S2CoronaConfirmedEntity

    @Query("SELECT * FROM corona_data_deaths_s2")
    fun getAllDeaths(): S2CoronaDeathsEntity

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveRecovered(t: S2CoronaRecoveredEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveConfirmed(t: S2CoronaConfirmedEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun saveDeaths(t: S2CoronaDeathsEntity)


    @Query("DELETE FROM corona_data_deaths_s2")
    suspend fun deleteAllDeatsh()

    @Query("DELETE FROM corona_data_confirmed_s2")
    suspend fun deleteAllConfirmed()

    @Query("DELETE FROM corona_data_recovered_s2")
    suspend fun deleteAllRecovered()

}