package co.kyald.coronavirustracking.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.kyald.coronavirustracking.data.database.converter.EntryConverter
import co.kyald.coronavirustracking.data.model.CoronaEntity
import co.kyald.coronavirustracking.data.model.DummyEntity
import co.kyald.coronavirustracking.data.database.dao.CoronaDao
import co.kyald.coronavirustracking.data.database.dao.DummyDao

@Database(
    entities = [DummyEntity::class, CoronaEntity::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(EntryConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dummyDao(): DummyDao
    abstract fun coronaDao(): CoronaDao
}