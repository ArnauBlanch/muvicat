package xyz.arnau.muvicat.remote.test

import xyz.arnau.muvicat.remote.model.gencat.GencatShowing
import xyz.arnau.muvicat.remote.model.gencat.GencatShowingResponse
import xyz.arnau.muvicat.remote.model.tmdb.TMDBSearchedMovie
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomInt
import xyz.arnau.muvicat.utils.DataFactory.Factory.randomString

class TMDBSearchedMovieFactory {
    companion object Factory {
        fun makeTMDBSearchedMovieModel(): TMDBSearchedMovie =
            TMDBSearchedMovie(
                id = randomInt(),
                original_title = randomString(),
                genre_ids = listOf(),
                backdrop_path = randomString()
            )

        fun makeTMDBSearchedMovieModelWithEmptyBackdropPath(): TMDBSearchedMovie =
            TMDBSearchedMovie(
                id = randomInt(),
                original_title = randomString(),
                genre_ids = listOf(),
                backdrop_path = ""
            )
    }
}