package xyz.arnau.muvicat.data.repository

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.data.model.Showing

interface ShowingCache {
    fun getShowings(): LiveData<List<Showing>>
    fun updateShowings(showings: List<Showing>)
}