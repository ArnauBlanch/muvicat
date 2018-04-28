package xyz.arnau.muvicat.viewmodel.movie

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import org.junit.Assert.assertEquals
import org.junit.Before
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
class MovieListViewModelTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val movieRepository = mock(MovieRepository::class.java)
    private lateinit var movieListViewModel: MovieListViewModel
    private val moviesLiveData = MutableLiveData<Resource<List<Movie>>>()

    @Before
    fun setUp() {
        `when`(movieRepository.getMovies()).thenReturn(moviesLiveData)
        movieListViewModel = MovieListViewModel(movieRepository)
    }

    @Test
    fun getMoviesReturnsLiveData() {
        val movies = MovieFactory.makeMovieList(5)
        moviesLiveData.postValue(Resource.success(movies))

        val result = movieListViewModel.movies.getValueBlocking()
        assertEquals(Status.SUCCESS, result!!.status)
        assertEquals(null, result.message)
        assertEquals(movies, result.data)
    }
}