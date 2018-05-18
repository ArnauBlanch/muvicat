package xyz.arnau.muvicat.repository.data

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.repository.model.Cinema

interface CinemaCache {
    fun getCinemas(): LiveData<List<Cinema>>
    fun getCinema(cinemaId: Long): LiveData<Cinema>
    fun updateCinemas(cinemas: List<CinemaEntity>)
}