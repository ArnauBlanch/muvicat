package xyz.arnau.muvicat.cache.mapper

import xyz.arnau.muvicat.cache.model.CachedMovie
import xyz.arnau.muvicat.data.model.MovieEntity

open class MovieEntityMapper :
    EntityMapper<CachedMovie, MovieEntity> {
    override fun mapToCached(type: MovieEntity) =
        CachedMovie(
            type.id, type.title, type.originalTitle, type.year, type.direction,
            type.cast, type.plot, type.releaseDate, type.posterUrl, type.priority,
            type.originalLanguage, type.ageRating, type.trailerUrl
        )

    override fun mapFromCached(type: CachedMovie) =
        MovieEntity(
            type.id, type.title, type.originalTitle, type.year, type.direction,
            type.cast, type.plot, type.releaseDate, type.posterUrl, type.priority,
            type.originalLanguage, type.ageRating, type.trailerUrl
        )
}