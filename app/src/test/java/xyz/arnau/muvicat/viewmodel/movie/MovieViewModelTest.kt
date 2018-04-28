package xyz.arnau.muvicat.viewmodel.movie

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import xyz.arnau.muvicat.data.MovieRepository
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.data.test.MovieFactory
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class MovieViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val movieRepository = mock(MovieRepository::class.java)
    private val movieViewModel = MovieViewModel(movieRepository)

    @Test
    fun getMovieReturnsLiveData() {
        val movie = MovieFactory.makeMovie()
        movieViewModel.setId(movie.id)

        val movieLiveData = MutableLiveData<Resource<Movie>>()
        `when`(movieRepository.getMovie(movie.id)).thenReturn(movieLiveData)
        movieLiveData.postValue(Resource.success(movie))

        val result = movieViewModel.movie.getValueBlocking()
        assertEquals(Status.SUCCESS, result!!.status)
        assertEquals(null, result.message)
        assertEquals(movie, result.data)
    }
}