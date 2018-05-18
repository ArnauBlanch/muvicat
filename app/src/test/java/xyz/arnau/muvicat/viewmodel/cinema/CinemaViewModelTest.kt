package xyz.arnau.muvicat.viewmodel.cinema

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import xyz.arnau.muvicat.repository.CinemaRepository
import xyz.arnau.muvicat.repository.MovieRepository
import xyz.arnau.muvicat.repository.ShowingRepository
import xyz.arnau.muvicat.repository.model.*
import xyz.arnau.muvicat.repository.test.*
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class CinemaViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val cinemaRepository = mock(CinemaRepository::class.java)
    private val showingRepository = mock(ShowingRepository::class.java)
    private val movieRepository = mock(MovieRepository::class.java)
    private val cinemaViewModel = CinemaViewModel(cinemaRepository, showingRepository, movieRepository)
    private val cinemaShowingsLiveData = MutableLiveData<Resource<List<CinemaShowing>>>()
    private val moviesLiveData = MutableLiveData<Resource<List<Movie>>>()

    @Test
    fun getCinemaReturnsLiveData() {
        val cinema = CinemaFactory.makeCinema()
        cinemaViewModel.setId(cinema.id)

        val cinemaLiveData = MutableLiveData<Resource<Cinema>>()
        `when`(cinemaRepository.getCinema(cinema.id)).thenReturn(cinemaLiveData)
        cinemaLiveData.postValue(Resource.success(cinema))

        val result = cinemaViewModel.cinema.getValueBlocking()
        assertEquals(Status.SUCCESS, result!!.status)
        assertEquals(null, result.message)
        assertEquals(cinema, result.data)
    }

    @Test
    fun getShowingsReturnsLiveData() {
        val cinemaId = 100.toLong()
        cinemaViewModel.setId(cinemaId)
        val cinemaShowings = CinemaShowingMapper.mapFromShowingEntityList(ShowingEntityFactory.makeShowingEntityList(5))
        `when`(showingRepository.getShowingsByCinema(cinemaId)).thenReturn(cinemaShowingsLiveData)
        cinemaShowingsLiveData.postValue(Resource.success(cinemaShowings))

        val result = cinemaViewModel.showings.getValueBlocking()
        Mockito.verify(showingRepository).getShowingsByCinema(cinemaId)
        assertEquals(Status.SUCCESS, result!!.status)
        assertEquals(null, result.message)
        assertEquals(cinemaShowings, result.data)
    }

    @Test
    fun getMoviesReturnsLiveData() {
        val cinemaId = 100.toLong()
        cinemaViewModel.setId(cinemaId)
        val movies = MovieMapper.mapFromMovieEntityList(MovieEntityFactory.makeMovieEntityList(5))
        `when`(movieRepository.getMoviesByCinema(cinemaId)).thenReturn(moviesLiveData)
        moviesLiveData.postValue(Resource.success(movies))

        val result = cinemaViewModel.movies.getValueBlocking()
        Mockito.verify(movieRepository).getMoviesByCinema(cinemaId)
        Assert.assertEquals(Status.SUCCESS, result!!.status)
        Assert.assertEquals(null, result.message)
        Assert.assertEquals(movies, result.data)
    }
}