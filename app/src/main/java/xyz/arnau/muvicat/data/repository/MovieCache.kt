package xyz.arnau.muvicat.data.repository

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.data.model.Movie

interface MovieCache {
    fun getMovies(): LiveData<List<Movie>>
    fun getMovie(movieId: Long): LiveData<Movie>
    fun updateMovies(movies: List<Movie>)
    fun clearMovies()
    fun isExpired(): Boolean
}