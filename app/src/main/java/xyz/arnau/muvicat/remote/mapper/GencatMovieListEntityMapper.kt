package xyz.arnau.muvicat.remote.mapper

import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.remote.model.GencatMovieResponse

class GencatMovieListEntityMapper constructor(private val itemMapper: GencatMovieEntityMapper) :
        EntityMapper<GencatMovieResponse?, List<Movie>?> {

    override fun mapFromRemote(type: GencatMovieResponse?): List<Movie>? =
            type?.moviesList?.mapNotNull { itemMapper.mapFromRemote(it) }
}