package xyz.arnau.muvicat.repository.test

import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.repository.model.MovieWithCast

object MovieWithCastMapper {
    fun mapFromMovieEntity(movie: MovieEntity) =
        MovieWithCast(MovieMapper.mapFromMovieEntity(movie))
}