@file:Suppress("DEPRECATION")

package xyz.arnau.muvicat.di

import android.content.Context
import dagger.Module
import dagger.Provides
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.remote.GencatRemoteImpl
import xyz.arnau.muvicat.remote.mapper.*
import xyz.arnau.muvicat.remote.service.GencatService
import xyz.arnau.muvicat.remote.utils.LiveDataCallAdapterFactory
import xyz.arnau.muvicat.remote.utils.RemotePreferencesHelper
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

@Module
class RemoteModule {
    @Singleton
    @Provides
    fun provideRemotePreferencesHelper(context: Context): RemotePreferencesHelper {
        return RemotePreferencesHelper(context)
    }

    @Singleton
    @Provides
    fun provideOkHttpClient(httpLoggingInterceptor: HttpLoggingInterceptor): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .build()

    }

    @Singleton
    @Provides
    fun provideLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (isDebug)
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.NONE
        return logging
    }

    @Singleton
    @Provides
    fun provideGencatService(): GencatService {
        return provideGencatService(true)
    }

    private fun provideGencatService(isDebug: Boolean): GencatService {
        val okHttpClient = provideOkHttpClient(provideLoggingInterceptor(isDebug))
        return makeGencatService(okHttpClient)
    }

    @Singleton
    @Provides
    fun provideGencatRemote(gencatService: GencatService, preferencesHelper: RemotePreferencesHelper): GencatRemote {
        return GencatRemoteImpl(
            gencatService,
            preferencesHelper,
            GencatMovieListEntityMapper(GencatMovieEntityMapper()),
            GencatCinemaListEntityMapper(GencatCinemaEntityMapper()),
            GencatShowingListEntityMapper(GencatShowingEntityMapper())
        )
    }

    private fun makeGencatService(okHttpClient: OkHttpClient): GencatService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://gencat.cat/llengua/cinema/")
            .client(okHttpClient)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
        return retrofit.create(GencatService::class.java)
    }

}