package co.kyald.coronavirustracking

import android.app.Application
import co.kyald.coronavirustracking.injection.*
import org.koin.android.ext.android.startKoin
import timber.log.Timber


class MainApplication : Application() {
    override fun onCreate() {
        super.onCreate()


        Timber.plant(Timber.DebugTree())

        startKoin(this,
            listOf(
                appModule,
                networkModule,
                repositoryModule,
                viewModelModule,
                databaseModule,
                sharedPreferencesModule
            )
        )
    }
}