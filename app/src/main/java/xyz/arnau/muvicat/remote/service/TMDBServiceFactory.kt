@file:Suppress("DEPRECATION")

package xyz.arnau.muvicat.remote.service

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import xyz.arnau.muvicat.remote.utils.LiveDataCallAdapterFactory
import java.util.concurrent.TimeUnit

object TMDBServiceFactory {
    fun makeTMDBService(context: Context, isDebug: Boolean): TMDBService {
        val okHttpClient = makeOkHttpClient(
            makeLoggingInterceptor(isDebug), context
        )
        return makeTMDBService(okHttpClient)
    }

    fun makeTMDBService(okHttpClient: OkHttpClient): TMDBService {
        val retrofit = Retrofit.Builder()
            .baseUrl("https://api.themoviedb.org/3/")
            .client(okHttpClient)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(GsonConverterFactory.create())
            .build()
        return retrofit.create(TMDBService::class.java)
    }

    private fun makeOkHttpClient(
        httpLoggingInterceptor: HttpLoggingInterceptor,
        context: Context
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(httpLoggingInterceptor)
            .connectTimeout(120, TimeUnit.SECONDS)
            .readTimeout(120, TimeUnit.SECONDS)
            .cache(Cache(context.cacheDir, 1024 * 1024 * 4))
            .build()

    }

    private fun makeLoggingInterceptor(isDebug: Boolean): HttpLoggingInterceptor {
        val logging = HttpLoggingInterceptor()
        logging.level = if (isDebug)
            HttpLoggingInterceptor.Level.BODY
        else
            HttpLoggingInterceptor.Level.NONE
        return logging
    }


}