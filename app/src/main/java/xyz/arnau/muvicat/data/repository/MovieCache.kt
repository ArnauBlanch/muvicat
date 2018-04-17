package xyz.arnau.muvicat.data.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import xyz.arnau.muvicat.data.model.MovieEntity

interface MovieCache {
    fun clearMovies(): Completable
    fun saveMovies(movies: List<MovieEntity>): Completable
    fun getMovies(): Flowable<List<MovieEntity>>
    fun isCached(): Single<Boolean>
    fun setLastCacheTime(lastCache: Long)
    fun isExpired(): Boolean
}