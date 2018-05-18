package xyz.arnau.muvicat.repository.data

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.repository.model.CinemaShowing
import xyz.arnau.muvicat.repository.model.MovieShowing
import xyz.arnau.muvicat.repository.model.Showing

interface ShowingCache {
    fun getShowings(): LiveData<List<Showing>>
    fun getShowingsByCinema(cinemaId: Long): LiveData<List<CinemaShowing>>
    fun getShowingsByMovie(movieId: Long): LiveData<List<MovieShowing>>
    fun updateShowings(showings: List<ShowingEntity>): Boolean
}