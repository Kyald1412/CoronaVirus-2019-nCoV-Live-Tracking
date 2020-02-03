package co.kyald.coronavirustracking.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import co.kyald.coronavirustracking.data.database.converter.EntryConverter
import co.kyald.coronavirustracking.data.model.CoronaEntity
import co.kyald.coronavirustracking.data.model.DummyEntity
import co.kyald.coronavirustracking.data.database.dao.CoronaDao
import co.kyald.coronavirustracking.data.database.dao.CountryCoordDao
import co.kyald.coronavirustracking.data.database.dao.DummyDao
import co.kyald.coronavirustracking.data.model.CountryCoordEntity

@Database(
    entities = [DummyEntity::class, CountryCoordEntity::class, CoronaEntity::class],
    version = 2,
    exportSchema = false
)
@TypeConverters(EntryConverter::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dummyDao(): DummyDao
    abstract fun coronaDao(): CoronaDao
    abstract fun countryCoordDao(): CountryCoordDao
}