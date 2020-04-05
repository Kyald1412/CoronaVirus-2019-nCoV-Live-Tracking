package co.kyald.coronavirustracking.data.database.dao.arcgis

import androidx.room.Dao
import androidx.room.Query
import co.kyald.coronavirustracking.data.database.dao.BaseDao
import co.kyald.coronavirustracking.data.database.model.arcgis.S3CoronaEntity

@Dao
interface S3CoronaDao : BaseDao<S3CoronaEntity> {

    @Query("SELECT * FROM corona_s3_entity")
    fun getAllCases(): List<S3CoronaEntity>

    @Query("SELECT SUM(attr_confirmed) FROM corona_s3_entity")
    fun getTotalConfirmedCase(): Int

    @Query("SELECT SUM(attr_deaths) FROM corona_s3_entity")
    fun getTotalDeathCase(): Int

    @Query("SELECT SUM(attr_recovered) FROM corona_s3_entity")
    fun getTotalRecoveredCase(): Int

    @Query("SELECT SUM(attr_active) FROM corona_s3_entity")
    fun getTotalActiveCase(): Int

    @Query("DELETE FROM corona_s3_entity")
    fun deleteAll()

}