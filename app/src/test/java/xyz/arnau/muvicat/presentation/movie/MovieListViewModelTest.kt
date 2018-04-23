package xyz.arnau.muvicat.presentation.movie

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.`when`
import org.mockito.Mockito.mock
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.model.Status
import xyz.arnau.muvicat.data.repository.MovieRepository
import xyz.arnau.muvicat.data.test.MovieFactory
import xyz.arnau.muvicat.utils.getValueBlocking

@RunWith(JUnit4::class)
class MovieListViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val movieRepository = mock(MovieRepository::class.java)
    private val movieListViewModel = MovieListViewModel(movieRepository)

    @Test
    fun getMoviesReturnsLiveData() {
        val moviesLiveData = MutableLiveData<Resource<List<Movie>>>()
        `when`(movieListViewModel.movies).thenReturn(moviesLiveData)
        val movies = MovieFactory.makeMovieList(5)
        moviesLiveData.postValue(Resource.success(movies))

        val result = moviesLiveData.getValueBlocking()
        assertEquals(Status.SUCCESS, result!!.status)
        assertEquals(null, result.message)
        assertEquals(movies, result.data)
    }
}