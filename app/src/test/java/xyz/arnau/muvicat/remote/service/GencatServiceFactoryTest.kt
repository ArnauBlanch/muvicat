package xyz.arnau.muvicat.remote.service

import android.content.Context
import okhttp3.OkHttpClient
import org.junit.Test
import org.mockito.Mockito.mock

class GencatServiceFactoryTest {
    @Test
    fun makeGencatServiceFromOkHttpClient() {
        GencatServiceFactory.makeGencatService(OkHttpClient())
    }

    @Test
    fun makeGencatServiceFromContextDebug() {
        GencatServiceFactory.makeGencatService(mock(Context::class.java), true)
    }

    @Test
    fun makeGencatServiceFromContextNoDebug() {
        GencatServiceFactory.makeGencatService(mock(Context::class.java), false)
    }
}