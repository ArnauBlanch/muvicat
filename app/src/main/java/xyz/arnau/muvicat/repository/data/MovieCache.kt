package xyz.arnau.muvicat.repository.data

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.cache.model.MovieExtraInfo
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.model.MovieWithCast

interface MovieCache {
    fun getMovies(): LiveData<List<Movie>>
    fun getMoviesByCinema(cinemaId: Long): LiveData<List<Movie>>
    fun getMovie(movieId: Long): LiveData<MovieWithCast>
    fun updateMovies(movies: List<MovieEntity>)
    fun updateExtraMovieInfo(movieId: Long, extraInfo: MovieExtraInfo)
    fun voteMovie(movieId: Long, vote: Double)
    fun unvoteMovie(movieId: Long)
}