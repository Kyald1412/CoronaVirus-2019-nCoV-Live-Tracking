package co.kyald.coronavirustracking.injection

import co.kyald.coronavirustracking.data.network.NetworkConstants
import co.kyald.coronavirustracking.data.network.category.CoronaS1Api
import co.kyald.coronavirustracking.data.network.category.CoronaS2Api
import co.kyald.coronavirustracking.data.network.category.DummyApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.HttpUrl
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.core.qualifier.named
import org.koin.dsl.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

val networkModule = module {

    single(named("S2")) {
        Retrofit.Builder()
            .client(get())
            .baseUrl(NetworkConstants.BASE_URL_S2)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single(named("S1")) {
        Retrofit.Builder()
            .client(get())
            .baseUrl(NetworkConstants.BASE_URL_S1)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    single { provideDefaultOkHttpClient() }
    single { provideDummyService(get()) }
    single { provideCoronaS1Service(get(named("S1"))) }
    single { provideCoronaS2Service(get(named("S2"))) }
}


private val logger: HttpLoggingInterceptor
    get() {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.apply { loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS }
            .level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }


fun provideDefaultOkHttpClient(): OkHttpClient {
    val builder = OkHttpClient().newBuilder()
    builder.addInterceptor(logger)

    return builder.build()
}

fun provideDummyService(retrofit: Retrofit): DummyApi =
    retrofit.create(DummyApi::class.java)

fun provideCoronaS1Service(retrofit: Retrofit): CoronaS1Api =
    retrofit.create(CoronaS1Api::class.java)

fun provideCoronaS2Service(retrofit: Retrofit): CoronaS2Api =
    retrofit.create(CoronaS2Api::class.java)