package co.kyald.coronavirustracking.injection

import org.koin.dsl.module.module
import java.util.*

val appModule = module {
    single { provideCalendar() }
}

private fun provideCalendar() = Calendar.getInstance()
