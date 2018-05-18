package xyz.arnau.muvicat.viewmodel.movie

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.Mockito.*
import xyz.arnau.muvicat.repository.MovieRepository
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.model.Resource
import xyz.arnau.muvicat.repository.model.Status
import xyz.arnau.muvicat.repository.test.MovieEntityFactory
import xyz.arnau.muvicat.repository.test.MovieMapper
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
        val movies = MovieMapper.mapFromMovieEntityList(MovieEntityFactory.makeMovieEntityList(5))
        moviesLiveData.postValue(Resource.success(movies))

        val result = movieListViewModel.movies.getValueBlocking()
        verify(movieRepository).getMovies()
        assertEquals(Status.SUCCESS, result!!.status)
        assertEquals(null, result.message)
        assertEquals(movies, result.data)
    }
}