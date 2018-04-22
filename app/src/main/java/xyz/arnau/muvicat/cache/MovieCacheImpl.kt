package xyz.arnau.muvicat.cache

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.data.PreferencesHelper
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.repository.MovieCache
import javax.inject.Inject

class MovieCacheImpl @Inject constructor(
        private val muvicatDatabase: MuvicatDatabase,
        private val preferencesHelper: PreferencesHelper
) : MovieCache {

    companion object {
        const val EXPIRATION_TIME: Long = (3 * 60 * 60 * 1000).toLong() // 3 hours
    }

    override fun clearMovies() = muvicatDatabase.movieDao().clearMovies()

    override fun getMovies(): LiveData<List<Movie>> {
        return muvicatDatabase.movieDao().getMovies()
    }

    override fun setLastCacheTime(lastCache: Long) {
        preferencesHelper.lastCacheTime = lastCache
    }

    private fun getLastCacheUpdateTimeMillis(): Long =
            preferencesHelper.lastCacheTime

    override fun isExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = this.getLastCacheUpdateTimeMillis()
        return currentTime - lastUpdateTime > EXPIRATION_TIME
    }

    override fun isCached(): Boolean = muvicatDatabase.movieDao().isCached()

    override fun saveMovies(movies: List<Movie>) {
        movies.forEach { muvicatDatabase.movieDao().insertMovie(it) }
    }
}