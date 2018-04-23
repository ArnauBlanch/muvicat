package xyz.arnau.muvicat.data

import android.arch.core.executor.testing.InstantTaskExecutorRule
import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.Observer
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.JUnit4
import org.mockito.ArgumentMatchers
import org.mockito.Mockito
import org.mockito.Mockito.*
import xyz.arnau.muvicat.AppExecutors
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource
import xyz.arnau.muvicat.data.repository.GencatRemote
import xyz.arnau.muvicat.data.repository.MovieCache
import xyz.arnau.muvicat.data.repository.MovieRepository
import xyz.arnau.muvicat.data.test.MovieFactory
import xyz.arnau.muvicat.remote.model.Response
import xyz.arnau.muvicat.remote.model.ResponseStatus
import xyz.arnau.muvicat.utils.InstantAppExecutors

@RunWith(JUnit4::class)
class MovieRepositoryTest {
    @get:Rule
    val rule = InstantTaskExecutorRule()

    private val movieCache: MovieCache = mock(MovieCache::class.java)
    private val gencatRemote: GencatRemote = mock(GencatRemote::class.java)
    private val appExecutors: AppExecutors = InstantAppExecutors()
    private val preferencesHelper: PreferencesHelper = mock(PreferencesHelper::class.java)

    private val movieRepository =
            MovieRepository(movieCache, gencatRemote, appExecutors, preferencesHelper)

    @Test
    fun getMoviesWhenMoviesAreCachedAndNotExpired() {
        val dbMovieLiveData = MutableLiveData<List<Movie>>()
        `when`(movieCache.getMovies()).thenReturn(dbMovieLiveData)
        val dbMovies = MovieFactory.makeMovieList(3)
        dbMovieLiveData.postValue(dbMovies)
        `when`(movieCache.isCached()).thenReturn(true)
        `when`(movieCache.isExpired()).thenReturn(false)

        val movies = movieRepository.getMovies()
        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)
        verify(observer).onChanged(Resource.success(dbMovies))
        verify(preferencesHelper, never()).moviesUpdated()
        verify(gencatRemote, never()).getMovies(ArgumentMatchers.anyString())
    }

    @Test
    fun getMoviesWhenMoviesAreCachedButExpired() {
        val dbMovieLiveData = MutableLiveData<List<Movie>>()
        `when`(movieCache.getMovies()).thenReturn(dbMovieLiveData)
        val dbMovies = MovieFactory.makeMovieList(3)
        `when`(movieCache.isCached()).thenReturn(true)
        `when`(movieCache.isExpired()).thenReturn(true)
        `when`(preferencesHelper.moviesETag).thenReturn("movie-etag")
        val remoteMovieLiveData = MutableLiveData<Response<List<Movie>>>()
        val remoteMovies = MovieFactory.makeMovieList(3)
        `when`(gencatRemote.getMovies("movie-etag"))
                .thenReturn(remoteMovieLiveData)


        val movies = movieRepository.getMovies()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)
        dbMovieLiveData.postValue(dbMovies)
        verify(observer).onChanged(Resource.loading(dbMovies))
        remoteMovieLiveData.postValue(Response(remoteMovies, null,
                ResponseStatus.SUCCESSFUL, "movie-etag2"))
        dbMovieLiveData.postValue(remoteMovies)
        verify(observer).onChanged(Resource.success(remoteMovies))
        verify(preferencesHelper).moviesETag = "movie-etag2"
        verify(preferencesHelper).moviesUpdated()
        verify(movieCache).updateMovies(remoteMovies)
    }

    @Test
    fun getMoviesWhenMoviesAreCachedButExpiredNotModified() {
        val dbMovieLiveData = MutableLiveData<List<Movie>>()
        `when`(movieCache.getMovies()).thenReturn(dbMovieLiveData)
        val dbMovies = MovieFactory.makeMovieList(3)
        `when`(movieCache.isCached()).thenReturn(true)
        `when`(movieCache.isExpired()).thenReturn(true)
        `when`(preferencesHelper.moviesETag).thenReturn("movie-etag")
        val remoteMovieLiveData = MutableLiveData<Response<List<Movie>>>()
        `when`(gencatRemote.getMovies("movie-etag"))
                .thenReturn(remoteMovieLiveData)


        val movies = movieRepository.getMovies()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)
        dbMovieLiveData.postValue(dbMovies)
        verify(observer).onChanged(Resource.loading(dbMovies))
        remoteMovieLiveData.postValue(Response(null, null,
                ResponseStatus.NOT_MODIFIED, null))
        verify(observer).onChanged(Resource.success(dbMovies))
        verify(preferencesHelper).moviesUpdated()
        verify(movieCache, never()).updateMovies(Mockito.anyList())
    }

    @Test
    fun getMoviesWhenMoviesAreNotCached() {
        val dbMovieLiveData = MutableLiveData<List<Movie>>()
        `when`(movieCache.getMovies()).thenReturn(dbMovieLiveData)
        val dbMovies = listOf<Movie>()
        `when`(movieCache.isCached()).thenReturn(false)
        `when`(preferencesHelper.moviesETag).thenReturn("movie-etag")
        val remoteMovieLiveData = MutableLiveData<Response<List<Movie>>>()
        val remoteMovies = MovieFactory.makeMovieList(3)
        `when`(gencatRemote.getMovies("movie-etag"))
                .thenReturn(remoteMovieLiveData)


        val movies = movieRepository.getMovies()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)
        dbMovieLiveData.postValue(dbMovies)
        verify(observer).onChanged(Resource.loading(dbMovies))
        remoteMovieLiveData.postValue(Response(remoteMovies, null,
                ResponseStatus.SUCCESSFUL, "movie-etag2"))
        dbMovieLiveData.postValue(remoteMovies)
        verify(observer).onChanged(Resource.success(remoteMovies))
        verify(preferencesHelper).moviesETag = "movie-etag2"
        verify(preferencesHelper).moviesUpdated()
        verify(movieCache).updateMovies(remoteMovies)
    }

    @Test
    fun getMoviesWhenMoviesAreNotCachedWithNullResponseBody() {
        val dbMovieLiveData = MutableLiveData<List<Movie>>()
        `when`(movieCache.getMovies()).thenReturn(dbMovieLiveData)
        val dbMovies = listOf<Movie>()
        `when`(movieCache.isCached()).thenReturn(false)
        `when`(preferencesHelper.moviesETag).thenReturn("movie-etag")
        val remoteMovieLiveData = MutableLiveData<Response<List<Movie>>>()
        `when`(gencatRemote.getMovies("movie-etag"))
                .thenReturn(remoteMovieLiveData)


        val movies = movieRepository.getMovies()

        val observer: Observer<*>? = mock(Observer::class.java)
        @Suppress("UNCHECKED_CAST")
        movies.observeForever(observer as Observer<Resource<List<Movie>>>)
        verify(observer).onChanged(Resource.loading(null))
        remoteMovieLiveData.postValue(Response(null, null,
                ResponseStatus.SUCCESSFUL, "movie-etag2"))
        dbMovieLiveData.postValue(dbMovies)
        verify(observer).onChanged(Resource.success(dbMovies))
        verify(preferencesHelper).moviesETag = "movie-etag2"
        verify(preferencesHelper, never()).moviesUpdated()
        verify(movieCache, never()).updateMovies(Mockito.anyList())
    }
}