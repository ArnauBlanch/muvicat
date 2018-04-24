package xyz.arnau.muvicat.cache

import android.arch.lifecycle.LiveData
import android.support.annotation.WorkerThread
import xyz.arnau.muvicat.cache.dao.MovieDao
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.repository.MovieCache
import xyz.arnau.muvicat.data.utils.PreferencesHelper
import javax.inject.Inject

class MovieCacheImpl @Inject constructor(
        private val movieDao: MovieDao,
        private val preferencesHelper: PreferencesHelper
) : MovieCache {

    companion object {
        const val EXPIRATION_TIME: Long = (3 * 60 * 60 * 1000).toLong() // $COVERAGE-IGNORE$
    }

    override fun clearMovies() = movieDao.clearMovies()

    override fun getMovies(): LiveData<List<Movie>> {
        return movieDao.getMovies()
    }

    override fun isExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = preferencesHelper.movieslastUpdateTime
        return currentTime - lastUpdateTime > EXPIRATION_TIME
    }

    @WorkerThread
    override fun isCached(): Boolean = movieDao.isCached()

    override fun updateMovies(movies: List<Movie>) {
        movieDao.updateMovieDb(movies)
    }
}