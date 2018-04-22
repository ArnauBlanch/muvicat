package xyz.arnau.muvicat.data.repository

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.remote.model.Response

interface GencatRemote {
    fun getMovies(): LiveData<Response<List<Movie>>>
}