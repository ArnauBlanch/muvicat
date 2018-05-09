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
import xyz.arnau.muvicat.data.model.CinemaInfo
import xyz.arnau.muvicat.data.test.CinemaFactory
import xyz.arnau.muvicat.data.test.CinemaInfoFactory

@RunWith(JUnit4::class)
class CinemaCacheImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val cinemaDao = mock(CinemaDao::class.java)

    private val cinemaCacheImpl = CinemaCacheImpl(cinemaDao)

    @Test
    fun getCinemasReturnsData() {
        val cinemas = CinemaInfoFactory.makeCinemaInfoList(5)
        val cinemasLiveData = MutableLiveData<List<CinemaInfo>>()
        cinemasLiveData.value = cinemas
        `when`(cinemaDao.getCinemas()).thenReturn(cinemasLiveData)
        val cinemasFromCache = cinemaCacheImpl.getCinemas()
        verify(cinemaDao).getCinemas()
        assertEquals(cinemas, cinemasFromCache.value)
    }

    @Test
    fun getCinemaReturnsCinemaInfo() {
        val cinema = CinemaInfoFactory.makeCinemaInfo()
        val cinemaLiveData = MutableLiveData<CinemaInfo>()
        cinemaLiveData.value = cinema
        `when`(cinemaDao.getCinema(cinema.id)).thenReturn(cinemaLiveData)
        val cinemaFromCache = cinemaCacheImpl.getCinema(cinema.id)
        verify(cinemaDao).getCinema(cinema.id)
        assertEquals(cinema, cinemaFromCache.value)
    }

    @Test
    fun updateCinemasUpdateData() {
        val cinemas = CinemaFactory.makeCinemaList(5)
        cinemaCacheImpl.updateCinemas(cinemas)

        verify(cinemaDao).updateCinemaDb(cinemas)
    }
}