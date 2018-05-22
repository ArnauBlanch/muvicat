package xyz.arnau.muvicat.cache

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import xyz.arnau.muvicat.cache.dao.MovieDao
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.cache.model.MovieExtraInfo
import xyz.arnau.muvicat.repository.model.Movie
import xyz.arnau.muvicat.repository.data.MovieCache
import xyz.arnau.muvicat.repository.model.MovieWithCast
import javax.inject.Inject

class MovieCacheImpl @Inject constructor(private val movieDao: MovieDao) : MovieCache {
    override fun getMovies(): LiveData<List<Movie>> {
        return movieDao.getCurrentMovies()
    }

    override fun getMoviesByCinema(cinemaId: Long): LiveData<List<Movie>> {
        return movieDao.getCurrentMoviesByCinema(cinemaId)
    }

    override fun getMovie(movieId: Long): LiveData<MovieWithCast> {
        return movieDao.getMovie(movieId)
    }

    override fun updateMovies(movies: List<MovieEntity>) {
        movieDao.updateMovieDb(movies)
    }

    override fun updateExtraMovieInfo(movieId: Long, extraInfo: MovieExtraInfo) {
        movieDao.addMovieExtraInfo(movieId, extraInfo)
    }

    override fun voteMovie(movieId: Long, vote: Double) {
        movieDao.voteMovie(movieId, vote)
    }
}