package co.kyald.coronavirustracking.injection

import co.kyald.coronavirustracking.data.repository.CoronaS1Repository
import co.kyald.coronavirustracking.data.repository.CoronaS2Repository
import co.kyald.coronavirustracking.data.repository.CoronaS3Repository
import co.kyald.coronavirustracking.data.repository.DummyRepository
import org.koin.dsl.module

val repositoryModule = module {
    single {
        DummyRepository(dummyDao = get(), dummyService = get())
    }
    single {
        CoronaS1Repository(s1CoronaDao = get(),coronaS1Service = get())
    }
    single {
        CoronaS2Repository(s2CoronaDao = get(),coronaS2Service = get())
    }
    single {
        CoronaS3Repository(s3CoronaDao = get(),coronaS3Service = get())
    }
}