package xyz.arnau.muvicat.data.repository

import io.reactivex.Single
import xyz.arnau.muvicat.data.model.Movie

interface GencatRemote {
    fun getMovies(): Single<List<Movie>>
}