package xyz.arnau.muvicat.viewmodel.movie

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import junit.framework.TestCase
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import xyz.arnau.muvicat.repository.MovieRepository
import xyz.arnau.muvicat.repository.ShowingRepository
import xyz.arnau.muvicat.repository.model.*
import xyz.arnau.muvicat.repository.test.*
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class MovieViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val movieRepository = mock(MovieRepository::class.java)
    private val showingRepository = mock(ShowingRepository::class.java)
    private val movieViewModel = MovieViewModel(movieRepository, showingRepository)

    private val movieWithCast = MovieWithCastMapper.mapFromMovieEntity(MovieEntityFactory.makeMovieEntity())
    private val movieShowingsLiveData = MutableLiveData<Resource<List<MovieShowing>>>()
    private val movieShowings = MovieShowingMapper.mapFromShowingEntityList(ShowingEntityFactory.makeShowingEntityList(5))

    @Test
    fun getMovieReturnsLiveData() {
        movieViewModel.setId(movieWithCast.movie.id)

        val movieLiveData = MutableLiveData<Resource<MovieWithCast>>()
        `when`(movieRepository.getMovie(movieWithCast.movie.id)).thenReturn(movieLiveData)
        movieLiveData.postValue(Resource.success(movieWithCast))

        val result = movieViewModel.movie.getValueBlocking()
        assertEquals(Status.SUCCESS, result!!.status)
        assertEquals(null, result.message)
        assertEquals(movieWithCast, result.data)
    }

    @Test
    fun getShowingsReturnsLiveData() {
        movieViewModel.setId(movieWithCast.movie.id)
        `when`(showingRepository.getShowingsByMovie(movieWithCast.movie.id)).thenReturn(movieShowingsLiveData)
        movieShowingsLiveData.postValue(Resource.success(movieShowings))

        val result = movieViewModel.showings.getValueBlocking()
        Mockito.verify(showingRepository).getShowingsByMovie(movieWithCast.movie.id)
        TestCase.assertEquals(Status.SUCCESS, result!!.status)
        TestCase.assertEquals(null, result.message)
        TestCase.assertEquals(movieShowings, result.data)
    }

    @Test
    fun rateMovieReturnsLiveData() {
        movieViewModel.setId(10.toLong())
        val liveData = MutableLiveData<Resource<Boolean>>()
        `when`(movieRepository.rateMovie(10.toLong(), 1, 1.0)).thenReturn(liveData)
        liveData.postValue(Resource.success(true))
        val result = movieViewModel.rateMovie(1, 1.0).getValueBlocking()

        assertEquals(Resource.success(true), result)
    }

    @Test
    fun rateMovieWithoutIdSet() {
        try {
            movieViewModel.rateMovie(1, 1.5)
            fail()
        } catch (e: NoSuchFieldException) {
            assertEquals("Unknown movie ID", e.message)
        }
    }
}