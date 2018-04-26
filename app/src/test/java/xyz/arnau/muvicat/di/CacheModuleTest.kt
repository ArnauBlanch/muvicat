package xyz.arnau.muvicat.di

import android.app.Application
import org.junit.Assert.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import org.robolectric.RobolectricTestRunner
import xyz.arnau.muvicat.cache.MovieCacheImpl
import xyz.arnau.muvicat.cache.dao.MovieDao
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.cache.db.MuvicatDatabase_Impl
import xyz.arnau.muvicat.data.utils.PreferencesHelper

@RunWith(RobolectricTestRunner::class)
class CacheModuleTest {
    private val cacheModule = CacheModule()

    @Test
    fun testProvideDb() {
        val app = mock(Application::class.java)
        assertEquals(MuvicatDatabase_Impl::class.java, cacheModule.provideDb(app).javaClass)
    }

    @Test
    fun testProvideMovieDao() {
        val db = mock(MuvicatDatabase::class.java)
        val movieDao = mock(MovieDao::class.java)
        `when`(db.movieDao()).thenReturn(movieDao)

        assertEquals(movieDao, cacheModule.provideMovieDao(db))
    }

    @Test
    fun testProvideMovieCache() {
        val movieDao = mock(MovieDao::class.java)
        val preferencesHelper = mock(PreferencesHelper::class.java)

        assertEquals(MovieCacheImpl::class.java, cacheModule.provideMovieCache(movieDao, preferencesHelper))
    }
}