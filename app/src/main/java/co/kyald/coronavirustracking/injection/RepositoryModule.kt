package co.kyald.coronavirustracking.injection

import co.kyald.coronavirustracking.data.repository.CoronaRepository
import co.kyald.coronavirustracking.data.repository.DummyRepository
import org.koin.dsl.module.module

val repositoryModule = module {
    single {
        DummyRepository(dummyDao = get(), dummyService = get())
        CoronaRepository(coronaDao = get(), coronaService = get())
    }
}