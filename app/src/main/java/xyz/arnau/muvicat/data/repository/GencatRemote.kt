package xyz.arnau.muvicat.data.repository

import io.reactivex.Flowable
import xyz.arnau.muvicat.data.model.MovieEntity

interface GencatRemote {
    fun getMovies(): Flowable<List<MovieEntity>>
}