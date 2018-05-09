package xyz.arnau.muvicat.data.repository

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.CinemaInfo

interface CinemaCache {
    fun getCinemas(): LiveData<List<CinemaInfo>>
    fun getCinema(cinemaId: Long): LiveData<CinemaInfo>
    fun updateCinemas(cinemas: List<Cinema>)
}