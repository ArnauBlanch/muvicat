package xyz.arnau.muvicat.data.repository

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.data.model.Movie

interface MovieCache {
    fun getMovies(): LiveData<List<Movie>>
    fun getMoviesByCinema(cinemaId: Long): LiveData<List<Movie>>
    fun getMovie(movieId: Long): LiveData<Movie>
    fun updateMovies(movies: List<MovieEntity>)
}