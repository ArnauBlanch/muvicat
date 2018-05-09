@file:Suppress("DEPRECATION")

package xyz.arnau.muvicat.remote.service

import android.content.Context
import okhttp3.Cache
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.simplexml.SimpleXmlConverterFactory
import xyz.arnau.muvicat.remote.utils.LiveDataCallAdapterFactory
import java.util.concurrent.TimeUnit

object GencatServiceFactory {
    fun makeGencatService(context: Context, isDebug: Boolean): GencatService {
        val okHttpClient = makeOkHttpClient(
            makeLoggingInterceptor(isDebug), context
        )
        return makeGencatService(okHttpClient)
    }

    fun makeGencatService(okHttpClient: OkHttpClient): GencatService {
        val retrofit = Retrofit.Builder()
            .baseUrl("http://gencat.cat/llengua/cinema/")
            .client(okHttpClient)
            .addCallAdapterFactory(LiveDataCallAdapterFactory())
            .addConverterFactory(SimpleXmlConverterFactory.create())
            .build()
        return retrofit.create(GencatService::class.java)
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