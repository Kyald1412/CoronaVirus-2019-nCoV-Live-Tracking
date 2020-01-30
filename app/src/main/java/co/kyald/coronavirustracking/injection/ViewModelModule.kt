package co.kyald.coronavirustracking.injection

import co.kyald.coronavirustracking.ui.feature.launchscreen.MainActivityViewModel
import org.koin.android.viewmodel.ext.koin.viewModel
import org.koin.dsl.module.module

val viewModelModule = module {
    viewModel { MainActivityViewModel(get()) }
}