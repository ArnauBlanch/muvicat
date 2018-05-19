package xyz.arnau.muvicat.remote.mapper

import org.junit.Assert.assertEquals
import org.junit.Test
import xyz.arnau.muvicat.cache.model.CastMemberEntity
import xyz.arnau.muvicat.cache.model.MovieExtraInfo
import xyz.arnau.muvicat.remote.model.gencat.GencatShowingResponse
import xyz.arnau.muvicat.remote.model.tmdb.TMDBCastMember
import xyz.arnau.muvicat.remote.model.tmdb.TMDBMovie
import xyz.arnau.muvicat.remote.model.tmdb.TMDBSearchedMovie
import xyz.arnau.muvicat.remote.test.GencatShowingFactory
import xyz.arnau.muvicat.remote.test.TMDBMovieFactory
import xyz.arnau.muvicat.remote.test.TMDBSearchedMovieFactory

class TMDBMovieInfoMapperTest {
    private val movieInfoMapper = TMDBMovieInfoMapper()

    @Test
    fun mapFromRemoteMapsData() {
        val searchedMovie = TMDBSearchedMovieFactory.makeTMDBSearchedMovieModel()
        val movie = TMDBMovieFactory.makeTMDBMovieModel()
        searchedMovie.genre_ids = listOf(TMDBMovieInfoMapper.genres.keys.first(), TMDBMovieInfoMapper.genres.keys.last())
        val genres = listOf(TMDBMovieInfoMapper.genres[searchedMovie.genre_ids[0]]!!, TMDBMovieInfoMapper.genres[searchedMovie.genre_ids[1]]!!)

        val mappedMovieInfo = movieInfoMapper.mapFromRemote(Pair(searchedMovie, movie))
        assertMovieInfo(searchedMovie, movie, mappedMovieInfo, genres)
    }

    @Test
    fun mapFromRemoteMapsDataWithEmptyBackdropUrl() {
        val searchedMovie = TMDBSearchedMovieFactory.makeTMDBSearchedMovieModelWithEmptyBackdropPath()
        val movie = TMDBMovieFactory.makeTMDBMovieModel()

        val mappedMovieInfo = movieInfoMapper.mapFromRemote(Pair(searchedMovie, movie))
        assertMovieInfo(searchedMovie, movie, mappedMovieInfo, listOf(), backdropNull = true)
    }

    @Test
    fun mapFromRemoteMapsDataWithZeroRuntime() {
        val searchedMovie = TMDBSearchedMovieFactory.makeTMDBSearchedMovieModel()
        val movie = TMDBMovieFactory.makeTMDBMovieModelWithZeroRuntime()

        val mappedMovieInfo = movieInfoMapper.mapFromRemote(Pair(searchedMovie, movie))
        assertMovieInfo(searchedMovie, movie, mappedMovieInfo, listOf(), runtimeNull = true)
    }

    private fun assertMovieInfo(searchedMovie: TMDBSearchedMovie, movie: TMDBMovie, mappedInfo: MovieExtraInfo, genres: List<String>,
                                backdropNull: Boolean = false, runtimeNull: Boolean = false) {
        assertEquals(searchedMovie.id, mappedInfo.tmdbId)
        assertEquals(if (runtimeNull) null else movie.runtime, mappedInfo.runtime)
        assertEquals(genres, mappedInfo.genres)
        assertEquals(if (backdropNull) null else searchedMovie.backdrop_path, mappedInfo.backdropUrl)
        assertEquals(movie.vote_average, mappedInfo.voteAverage)
        assertEquals(movie.vote_count, mappedInfo.voteCount)
        movie.credits.cast.sortedWith(compareBy<TMDBCastMember> { it.order }.thenBy { it.id }).take(10).
            forEachIndexed { index, castMember ->
            assertEquals(castMember.id, mappedInfo.cast[index].tmdbId)
            assertEquals((-1).toLong(), mappedInfo.cast[index].movieId)
            assertEquals(castMember.character, mappedInfo.cast[index].character)
            assertEquals(castMember.name, mappedInfo.cast[index].name)
            assertEquals(castMember.order, mappedInfo.cast[index].order)
            assertEquals(castMember.profile_path, mappedInfo.cast[index].profile_path)
        }
    }
}