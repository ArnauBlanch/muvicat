package xyz.arnau.muvicat.data.repository

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.remote.model.Response

interface GencatRemote {
    fun getMovies(eTag: String?): LiveData<Response<List<Movie>>>
    fun getCinemas(eTag: String?): LiveData<Response<List<Cinema>>>
}