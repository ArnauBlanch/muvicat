package xyz.arnau.muvicat.repository.data

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.remote.model.Response

interface GencatRemote {
    fun getMovies(): LiveData<Response<List<MovieEntity>>>
    fun getCinemas(): LiveData<Response<List<CinemaEntity>>>
    fun getShowings(): LiveData<Response<List<ShowingEntity>>>
}