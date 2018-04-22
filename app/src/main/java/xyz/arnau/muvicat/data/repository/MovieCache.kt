package xyz.arnau.muvicat.data.repository

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.data.model.Movie

interface MovieCache {
    fun clearMovies()
    /*fun saveMovies(movies: List<Movie>): Completable
    fun getMovies(): Flowable<List<Movie>>
    fun isCached(): Single<Boolean>*/
    fun getMovies(): LiveData<List<Movie>>

    fun saveMovies(movies: List<Movie>)
    fun setLastCacheTime(lastCache: Long)
    fun isExpired(): Boolean
    fun isCached(): Boolean
}