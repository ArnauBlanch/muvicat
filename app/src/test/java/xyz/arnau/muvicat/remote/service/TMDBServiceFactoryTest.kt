package xyz.arnau.muvicat.remote.service

import android.content.Context
import okhttp3.OkHttpClient
import org.junit.Test
import org.mockito.Mockito.mock

class TMDBServiceFactoryTest {
    @Test
    fun makeTMDBServiceFromOkHttpClient() {
        TMDBServiceFactory.makeTMDBService(OkHttpClient())
    }

    @Test
    fun makeTMDBServiceFromContextDebug() {
        TMDBServiceFactory.makeTMDBService(mock(Context::class.java), true)
    }

    @Test
    fun makeGencatServiceFromContextNoDebug() {
        TMDBServiceFactory.makeTMDBService(mock(Context::class.java), false)
    }
}