package xyz.santeri.citybike.inject

import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.moshi.MoshiConverterFactory
import xyz.santeri.citybike.BuildConfig
import xyz.santeri.citybike.data.remote.RacksApi
import xyz.santeri.citybike.http.HeaderInterceptor
import xyz.santeri.citybike.http.MockDataInterceptor
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class NetworkModule {
    companion object {
        const val BASE_URL = "https://data.foli.fi/"
        const val CONNECT_TIMEOUT = 10L
        const val READ_TIMEOUT = 10L
        const val WRITE_TIMEOUT = 10L
        const val USER_AGENT = "citybike-turku-android/${BuildConfig.VERSION_NAME}; (Android/*; +https://github.com/iffa/citybike-turku-android; me@santeri.xyz)"
    }

    @Provides
    @Singleton
    fun provideHttpClient(mockDataInterceptor: MockDataInterceptor): OkHttpClient {
        val builder = OkHttpClient.Builder()
                .addInterceptor(HeaderInterceptor(
                        Pair("User-Agent", USER_AGENT)
                ))
                .addInterceptor(mockDataInterceptor)
                .connectTimeout(CONNECT_TIMEOUT, TimeUnit.SECONDS)
                .readTimeout(READ_TIMEOUT, TimeUnit.SECONDS)
                .writeTimeout(WRITE_TIMEOUT, TimeUnit.SECONDS)

        return builder.build()
    }

    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(MoshiConverterFactory.create())
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    @Provides
    @Singleton
    fun provideRacksApi(retrofit: Retrofit): RacksApi = retrofit.create(RacksApi::class.java)
}