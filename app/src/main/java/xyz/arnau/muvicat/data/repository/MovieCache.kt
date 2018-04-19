package xyz.arnau.muvicat.data.repository

import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.remote.model.GencatMovieModel

interface MovieCache {
    fun clearMovies(): Completable
    fun saveMovies(movies: List<Movie>): Completable
    fun getMovies(): Flowable<List<Movie>>
    fun isCached(): Single<Boolean>
    fun setLastCacheTime(lastCache: Long)
    fun isExpired(): Boolean
}