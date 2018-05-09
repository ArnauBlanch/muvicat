package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.data.model.Movie

object MovieMapper {
    fun mapFromMovieEntity(movie: MovieEntity) =
        Movie(
            movie.id, movie.title, movie.originalTitle, movie.year, movie.direction,
            movie.cast, movie.plot, movie.releaseDate, movie.posterUrl, movie.priority,
            movie.originalLanguage, movie.ageRating, movie.trailerUrl
        )

    fun mapFromMovieEntityList(movieList: List<MovieEntity>) =
        movieList.map { mapFromMovieEntity(it) }
}