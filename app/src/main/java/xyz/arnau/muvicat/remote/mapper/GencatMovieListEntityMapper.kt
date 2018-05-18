package xyz.arnau.muvicat.remote.mapper

import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.remote.model.GencatMovieResponse

class GencatMovieListEntityMapper constructor(private val itemMapper: GencatMovieEntityMapper) :
    EntityMapper<GencatMovieResponse?, List<MovieEntity>?> {

    override fun mapFromRemote(type: GencatMovieResponse?): List<MovieEntity>? =
        type?.moviesList?.mapNotNull { itemMapper.mapFromRemote(it) }
}