package xyz.arnau.muvicat.data.repository

import android.arch.lifecycle.LiveData
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.data.model.Resource

interface MovieRepository {
    fun getMovies(): LiveData<Resource<List<Movie>>>
}
