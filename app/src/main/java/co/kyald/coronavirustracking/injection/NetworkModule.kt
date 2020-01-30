package co.kyald.coronavirustracking.injection

import co.kyald.coronavirustracking.data.network.NetworkConstants
import co.kyald.coronavirustracking.data.network.category.CoronaApi
import co.kyald.coronavirustracking.data.network.category.DummyApi
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.koin.dsl.module.module
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

val networkModule = module {
    single {
        provideDefaultOkHttpClient()
    }
    single { provideRetrofit(get()) }
    single { provideDummyService(get()) }
    single { provideCoronaService(get()) }
}


private val logger: HttpLoggingInterceptor
    get() {
        val loggingInterceptor = HttpLoggingInterceptor()
        loggingInterceptor.apply { loggingInterceptor.level = HttpLoggingInterceptor.Level.HEADERS }.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

fun provideDefaultOkHttpClient(): OkHttpClient {
    return OkHttpClient.Builder().addInterceptor(logger).build()
}

fun provideRetrofit(client: OkHttpClient): Retrofit {
    return Retrofit.Builder()
        .client(client)
        .baseUrl(NetworkConstants.BASE_URL)
//        .addConverterFactory(MoshiConverterFactory.create())
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

fun provideDummyService(retrofit: Retrofit): DummyApi =
    retrofit.create(DummyApi::class.java)

fun provideCoronaService(retrofit: Retrofit): CoronaApi =
    retrofit.create(CoronaApi::class.java)