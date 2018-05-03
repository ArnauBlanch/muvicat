package xyz.arnau.muvicat.cache

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import junit.framework.TestCase.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import xyz.arnau.muvicat.cache.dao.CinemaDao
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.test.CinemaFactory
import xyz.arnau.muvicat.data.utils.PreferencesHelper


@RunWith(JUnit4::class)
class CinemaCacheImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val preferencesHelper = mock(PreferencesHelper::class.java)
    private val cinemaDao = mock(CinemaDao::class.java)

    private val cinemaCacheImpl = CinemaCacheImpl(cinemaDao, preferencesHelper)

    @Test
    fun getCinemasReturnsData() {
        val cinemas = CinemaFactory.makeCinemaList(5)
        val cinemasLiveData = MutableLiveData<List<Cinema>>()
        cinemasLiveData.value = cinemas
        `when`(cinemaDao.getCinemas()).thenReturn(cinemasLiveData)
        val cinemasFromCache = cinemaCacheImpl.getCinemas()
        verify(cinemaDao).getCinemas()
        assertEquals(cinemas, cinemasFromCache.value)
    }

    @Test
    fun updateCinemasUpdateData() {
        val cinemas = CinemaFactory.makeCinemaList(5)
        cinemaCacheImpl.updateCinemas(cinemas)

        verify(cinemaDao).updateCinemaDb(cinemas)
    }

    @Test
    fun isExpiredReturnsTrueIfExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.cinemaslastUpdateTime)
            .thenReturn(currentTime - (CinemaCacheImpl.EXPIRATION_TIME + 500))
        assertEquals(true, cinemaCacheImpl.isExpired())
    }

    @Test
    fun isExpiredReturnsFalseIfNotExpired() {
        val currentTime = System.currentTimeMillis()
        `when`(preferencesHelper.cinemaslastUpdateTime)
            .thenReturn(currentTime - 5000)
        assertEquals(false, cinemaCacheImpl.isExpired())
    }

    @Test
    fun companionObjectTest() {
        assertEquals((3 * 60 * 60 * 1000).toLong(), CinemaCacheImpl.EXPIRATION_TIME)
    }
}