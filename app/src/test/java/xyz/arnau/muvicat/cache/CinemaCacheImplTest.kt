package xyz.arnau.muvicat.cache

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import junit.framework.TestCase.assertEquals
import org.joda.time.LocalDate
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import xyz.arnau.muvicat.cache.dao.CinemaDao
import xyz.arnau.muvicat.repository.model.Cinema
import xyz.arnau.muvicat.repository.test.CinemaEntityFactory
import xyz.arnau.muvicat.repository.test.CinemaFactory

@RunWith(JUnit4::class)
class CinemaCacheImplTest {
    @get:Rule
    var rule: TestRule = InstantTaskExecutorRule()

    private val cinemaDao = mock(CinemaDao::class.java)

    private val cinemaCacheImpl = CinemaCacheImpl(cinemaDao)

    @Test
    fun getCinemasReturnsData() {
        val today = LocalDate.now().toDate().time
        val cinemas = CinemaFactory.makeCinemaList(5)
        val cinemasLiveData = MutableLiveData<List<Cinema>>()
        cinemasLiveData.value = cinemas
        `when`(cinemaDao.getCurrentCinemas(today)).thenReturn(cinemasLiveData)
        val cinemasFromCache = cinemaCacheImpl.getCinemas()
        verify(cinemaDao).getCurrentCinemas(today)
        assertEquals(cinemas, cinemasFromCache.value)
    }

    @Test
    fun getCinemaReturnsCinemaInfo() {
        val cinema = CinemaFactory.makeCinema()
        val cinemaLiveData = MutableLiveData<Cinema>()
        cinemaLiveData.value = cinema
        `when`(cinemaDao.getCinema(cinema.id)).thenReturn(cinemaLiveData)
        val cinemaFromCache = cinemaCacheImpl.getCinema(cinema.id)
        verify(cinemaDao).getCinema(cinema.id)
        assertEquals(cinema, cinemaFromCache.value)
    }

    @Test
    fun updateCinemasUpdateData() {
        val cinemas = CinemaEntityFactory.makeCinemaEntityList(5)
        cinemaCacheImpl.updateCinemas(cinemas)

        verify(cinemaDao).updateCinemaDb(cinemas)
    }
}