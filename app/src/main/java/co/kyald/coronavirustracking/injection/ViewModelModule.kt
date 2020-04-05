package co.kyald.coronavirustracking.injection

import co.kyald.coronavirustracking.ui.feature.mainscreen.MainActivityViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { MainActivityViewModel(get(), get(), get(), get()) }
}