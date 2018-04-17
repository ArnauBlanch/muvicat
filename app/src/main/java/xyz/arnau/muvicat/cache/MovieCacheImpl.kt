package xyz.arnau.muvicat.cache

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import xyz.arnau.muvicat.cache.db.MuvicatDatabase
import xyz.arnau.muvicat.cache.mapper.MovieEntityMapper
import xyz.arnau.muvicat.data.model.MovieEntity
import xyz.arnau.muvicat.data.repository.MovieCache
import javax.inject.Inject

class MovieCacheImpl @Inject constructor(
    private val muvicatDatabase: MuvicatDatabase,
    private val entityMapper: MovieEntityMapper,
    private val preferencesHelper: PreferencesHelper
) : MovieCache {

    override fun clearMovies(): Completable =
        Completable.defer {
            muvicatDatabase.cachedMoviesDao().clearMovies()
            Completable.complete()
        }


    override fun saveMovies(movies: List<MovieEntity>): Completable =
        Completable.defer {
            movies.forEach {
                muvicatDatabase.cachedMoviesDao().insertMovie(
                    entityMapper.mapToCached(it)
                )
            }
            Completable.complete()
        }

    override fun getMovies(): Flowable<List<MovieEntity>> =
        Flowable.defer {
            Flowable.just(muvicatDatabase.cachedMoviesDao().getMovies())
        }.map {
            it.map { entityMapper.mapFromCached(it) }
        }

    override fun isCached(): Single<Boolean> =
        Single.defer {
            Single.just(muvicatDatabase.cachedMoviesDao().getMovies().isNotEmpty())
        }

    override fun setLastCacheTime(lastCache: Long) {
        preferencesHelper.lastCacheTime = lastCache
    }

    private fun getLastCacheUpdateTimeMillis(): Long =
        preferencesHelper.lastCacheTime

    private val EXPIRATION_TIME: Long = (3 * 60 * 60 * 1000).toLong() // 3 hours

    override fun isExpired(): Boolean {
        val currentTime = System.currentTimeMillis()
        val lastUpdateTime = this.getLastCacheUpdateTimeMillis()
        return currentTime - lastUpdateTime > EXPIRATION_TIME
    }

}