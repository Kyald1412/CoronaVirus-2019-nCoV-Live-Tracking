package co.kyald.coronavirustracking

import android.app.Application
import co.kyald.coronavirustracking.injection.*
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import timber.log.Timber

class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Timber.plant(Timber.DebugTree())

        startKoin {
            androidLogger()
            androidContext(this@MainApplication)
            modules(listOf(
                appModule,
                networkModule,
                repositoryModule,
                viewModelModule,
                databaseModule,
                sharedPreferencesModule
            ))

        }

    }
}