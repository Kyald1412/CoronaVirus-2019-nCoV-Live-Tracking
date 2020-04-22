package co.kyald.coronavirustracking.injection

import co.kyald.coronavirustracking.ui.feature.mainscreen.MainActivityViewModel
import co.kyald.coronavirustracking.ui.feature.menuscreen.fragment.graph.GraphViewModel
import co.kyald.coronavirustracking.ui.feature.menuscreen.fragment.trending.TrendingViewModel
import org.koin.dsl.module

val viewModelModule = module {
    factory { MainActivityViewModel(get(), get(), get(), get()) }
    factory { GraphViewModel() }
    factory { TrendingViewModel() }
}