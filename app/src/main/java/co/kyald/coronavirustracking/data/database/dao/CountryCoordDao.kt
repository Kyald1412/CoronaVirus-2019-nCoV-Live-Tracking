package co.kyald.coronavirustracking.data.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import co.kyald.coronavirustracking.data.model.CoronaEntity
import co.kyald.coronavirustracking.data.model.CountryCoordEntity
import com.google.gson.JsonArray
import org.json.JSONArray

@Dao
interface CountryCoordDao : BaseDao<CountryCoordEntity> {

    @Query("SELECT * FROM countrycoord")
    fun getCountryCoord(): List<CountryCoordEntity >

    @Query("DELETE FROM countrycoord")
    suspend fun deleteAllCountryCoord()
}