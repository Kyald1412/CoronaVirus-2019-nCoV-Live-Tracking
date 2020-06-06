package co.kyald.coronavirustracking.injection

import co.kyald.coronavirustracking.ui.feature.mainscreen.MainActivityViewModel
import co.kyald.coronavirustracking.ui.feature.menuscreen.MenuActivityViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

val viewModelModule = module {
    factory { MainActivityViewModel(get(), get(), get(), get()) }
    factory { MenuActivityViewModel(androidContext(),get())}
}