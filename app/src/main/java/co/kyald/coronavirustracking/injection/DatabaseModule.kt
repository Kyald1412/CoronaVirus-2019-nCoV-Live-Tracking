package co.kyald.coronavirustracking.injection

import android.content.Context
import androidx.room.Room
import co.kyald.coronavirustracking.data.database.AppDatabase
import org.koin.dsl.module


val databaseModule = module {
    single { provideAppDatabase(get()) }
    single { provideDummyDao(get()) }
    single { provideCoronaDao(get()) }
    single { provideCountryCoordDao(get()) }
}

const val DATABASE_NAME = "app_db"

private fun provideAppDatabase(context: Context): AppDatabase {
    return Room.databaseBuilder(
        context,
        AppDatabase::class.java,
        DATABASE_NAME
    ).build()
}

private fun provideDummyDao(database: AppDatabase) = database.dummyDao()
private fun provideCoronaDao(database: AppDatabase) = database.coronaDao()
private fun provideCountryCoordDao(database: AppDatabase) = database.countryCoordDao()