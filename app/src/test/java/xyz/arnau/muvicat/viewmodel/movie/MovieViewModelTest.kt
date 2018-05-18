package xyz.arnau.muvicat.viewmodel.movie

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import junit.framework.TestCase
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import xyz.arnau.muvicat.repository.MovieRepository
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.repository.ShowingRepository
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.model.MovieShowing
import xyz.arnau.muvicat.repository.model.Resource
import xyz.arnau.muvicat.repository.model.Status
import xyz.arnau.muvicat.repository.test.*
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class MovieViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val movieRepository = mock(MovieRepository::class.java)
    private val showingRepository = mock(ShowingRepository::class.java)
    private val movieViewModel = MovieViewModel(movieRepository, showingRepository)

    private val movie = MovieMapper.mapFromMovieEntity(MovieEntityFactory.makeMovieEntity())
    private val movieShowingsLiveData = MutableLiveData<Resource<List<MovieShowing>>>()
    private val movieShowings = MovieShowingMapper.mapFromShowingEntityList(ShowingEntityFactory.makeShowingEntityList(5))

    @Test
    fun getMovieReturnsLiveData() {
        movieViewModel.setId(movie.id)

        val movieLiveData = MutableLiveData<Resource<Movie>>()
        `when`(movieRepository.getMovie(movie.id)).thenReturn(movieLiveData)
        movieLiveData.postValue(Resource.success(movie))

        val result = movieViewModel.movie.getValueBlocking()
        assertEquals(Status.SUCCESS, result!!.status)
        assertEquals(null, result.message)
        assertEquals(movie, result.data)
    }

    @Test
    fun getShowingsReturnsLiveData() {
        movieViewModel.setId(movie.id)
        `when`(showingRepository.getShowingsByMovie(movie.id)).thenReturn(movieShowingsLiveData)
        movieShowingsLiveData.postValue(Resource.success(movieShowings))

        val result = movieViewModel.showings.getValueBlocking()
        Mockito.verify(showingRepository).getShowingsByMovie(movie.id)
        TestCase.assertEquals(Status.SUCCESS, result!!.status)
        TestCase.assertEquals(null, result.message)
        TestCase.assertEquals(movieShowings, result.data)
    }
}