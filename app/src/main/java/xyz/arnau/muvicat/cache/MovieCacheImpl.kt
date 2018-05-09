package xyz.arnau.muvicat.cache

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.dao.MovieDao
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.repository.MovieCache
import javax.inject.Inject

class MovieCacheImpl @Inject constructor(private val movieDao: MovieDao) : MovieCache {
    override fun getMovies(): LiveData<List<Movie>> {
        return movieDao.getMovies()
    }

    override fun getMovie(movieId: Long): LiveData<Movie> {
        return movieDao.getMovie(movieId)
    }

    override fun updateMovies(movies: List<Movie>) {
        movieDao.updateMovieDb(movies)
    }
}