package co.kyald.coronavirustracking.data.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy

@Dao
interface BaseDao<T> {

    /**
     * Insert or Update (Save) one entity, replacing in case of existence
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(t: T)

    /**
     * Insert or Update (Save) many entities, replacing in case of existence
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(vararg t: T)


    /**
     * Insert or Update (Save) a list of entities, replacing in case of existence
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun save(t: List<T>)


    /**
     * Insert one entity, aborting in case of existence
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(t: T)

    /**
     * Insert many entities, aborting in case of existence
     */
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(vararg t: T)

    /**
     * Delete one entity
     */
    @Delete
    suspend fun delete(t: T)

    /**
     * Delete many entities
     */
    @Delete
    suspend fun delete(vararg t: T)
}