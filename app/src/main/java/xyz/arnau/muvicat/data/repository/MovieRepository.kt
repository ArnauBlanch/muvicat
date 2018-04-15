package xyz.arnau.muvicat.data.repository

import xyz.arnau.muvicat.data.remote.GencatDataSource
import javax.inject.Inject

class MovieRepository @Inject constructor(private val gencatDataSource: GencatDataSource) {

    fun getMoviesFromApi() = gencatDataSource.requestMovies()

}