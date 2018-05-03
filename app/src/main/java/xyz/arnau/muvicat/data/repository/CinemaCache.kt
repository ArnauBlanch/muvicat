package xyz.arnau.muvicat.data.repository

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.data.model.Cinema

interface CinemaCache {
    fun getCinemas(): LiveData<List<Cinema>>
    fun updateCinemas(movies: List<Cinema>)
    fun isExpired(): Boolean
}