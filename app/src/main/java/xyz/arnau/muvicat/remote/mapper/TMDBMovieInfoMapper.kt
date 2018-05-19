package xyz.arnau.muvicat.remote.mapper

import xyz.arnau.muvicat.cache.model.CastMemberEntity
import xyz.arnau.muvicat.cache.model.MovieExtraInfo
import xyz.arnau.muvicat.remote.model.tmdb.TMDBCastMember
import xyz.arnau.muvicat.remote.model.tmdb.TMDBMovie
import xyz.arnau.muvicat.remote.model.tmdb.TMDBSearchedMovie

class TMDBMovieInfoMapper : EntityMapper<Pair<TMDBSearchedMovie, TMDBMovie>, MovieExtraInfo> {
    override fun mapFromRemote(type: Pair<TMDBSearchedMovie, TMDBMovie>): MovieExtraInfo {
        val searchedMovie = type.first
        val movie = type.second
        checkNullValues(searchedMovie, movie)
        return MovieExtraInfo(
            searchedMovie.id,
            movie.runtime,
            parseGenres(searchedMovie.genre_ids),
            searchedMovie.backdrop_path,
            movie.vote_average,
            movie.vote_count,
            mapCast(movie.credits.cast)
        )
    }

    private fun mapCast(cast: List<TMDBCastMember>): List<CastMemberEntity> {
        return cast.sortedWith(compareBy<TMDBCastMember> { it.order }.thenBy { it.id }).take(10)
            .map { CastMemberEntity(it.id, (-1).toLong(), it.order, it.name, it.character, it.profile_path) }
    }

    private fun parseGenres(genreIds: List<Int>): List<String> {
        return genreIds.mapNotNull { genres[it] }
    }

    private fun checkNullValues(searchedMovie: TMDBSearchedMovie, movie: TMDBMovie) {
        if (searchedMovie.backdrop_path == "")
            searchedMovie.backdrop_path = null
        if (movie.runtime != null && movie.runtime!! <= 0)
            movie.runtime = null
    }

    companion object {
        val genres: Map<Int, String> = mapOf(
            28 to "Acció",
            12 to "Aventura",
            16 to "Animació",
            35 to "Comèdia",
            80 to "Crim",
            99 to "Documental",
            18 to "Drama",
            10751 to "Familiar",
            14 to "Fantasia",
            36 to "Històrica",
            27 to "Terror",
            10402 to "Música",
            9648 to "Misteri",
            10749 to "Romàntica",
            878 to "Ciència ficció",
            10770 to "TV Movie",
            10752 to "Guerra",
            37 to "Western",
            53 to "Thriller"
        )
    }
}