package co.kyald.coronavirustracking.injection

import co.kyald.coronavirustracking.data.repository.*
import org.koin.dsl.module

val repositoryModule = module {
    single {
        DummyRepository(dummyDao = get(), dummyService = get())
    }
    single {
        CoronaS1Repository()
    }
    single {
        CoronaS2Repository(s2CoronaDao = get(),coronaS2Service = get())
    }
    single {
        CoronaS3Repository(s3CoronaDao = get(),coronaS3Service = get())
    }
    single {
        CoronaS4Repository(s4CoronaDao = get(),coronaS4Service = get())
    }
}