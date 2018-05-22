package xyz.arnau.muvicat.repository.test

import xyz.arnau.muvicat.cache.model.MovieEntity
import xyz.arnau.muvicat.repository.model.Movie

object MovieMapper {
    fun mapFromMovieEntity(movie: MovieEntity) =
        Movie(
            movie.id, movie.title, movie.originalTitle, movie.year, movie.direction,
            movie.cast, movie.plot, movie.releaseDate, movie.posterUrl, movie.priority,
            movie.originalLanguage, movie.ageRating, movie.trailerUrl,
            movie.tmdbId, movie.runtime, movie.genres, movie.backdropUrl, movie.voteAverage, movie.voteCount,
            movie.vote
        )

    fun mapFromMovieEntityList(movieList: List<MovieEntity>) =
        movieList.map { mapFromMovieEntity(it) }
}