package xyz.arnau.muvicat.repository.data

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.cache.model.MovieExtraInfo
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.remote.model.Response

interface TMDBRemote {
    fun getMovie(movieTitle: String): LiveData<Response<MovieExtraInfo>>
}