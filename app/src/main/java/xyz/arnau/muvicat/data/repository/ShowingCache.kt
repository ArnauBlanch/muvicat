package xyz.arnau.muvicat.data.repository

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.data.model.CinemaShowing
import xyz.arnau.muvicat.data.model.MovieShowing
import xyz.arnau.muvicat.data.model.Showing

interface ShowingCache {
    fun getShowings(): LiveData<List<Showing>>
    fun getShowingsByCinema(cinemaId: Long): LiveData<List<CinemaShowing>>
    fun getShowingsByMovie(movieId: Long): LiveData<List<MovieShowing>>
    fun updateShowings(showings: List<ShowingEntity>): Boolean
}