package xyz.arnau.muvicat.remote.test

import xyz.arnau.muvicat.remote.model.tmdb.TMDBSearchMovieResponse
import xyz.arnau.muvicat.remote.model.tmdb.TMDBSearchedMovie

object TMDBSampleSearchMovieResponse {
    val body = TMDBSearchMovieResponse(
        listOf(
            TMDBSearchedMovie(
                id = 438634,
                original_title = "Estiu 1993",
                genre_ids = listOf(18),
                backdrop_path = "/bhQXDHc2lCtI3EmjQFKh1BhvSK5.jpg"
            )
        )
    )

    val emptyBody = TMDBSearchMovieResponse(listOf())

    val json: String = """
        {
            "page": 1,
            "total_results": 1,
            "total_pages": 1,
            "results": [
                {
                    "vote_count": 39,
                    "id": 438634,
                    "video": false,
                    "vote_average": 6.8,
                    "title": "Summer 1993",
                    "popularity": 6.29642,
                    "poster_path": "/krdSl7IpDb9c31CXrzA5yfP1oYM.jpg",
                    "original_language": "ca",
                    "original_title": "Estiu 1993",
                    "genre_ids": [18],
                    "backdrop_path": "/bhQXDHc2lCtI3EmjQFKh1BhvSK5.jpg",
                    "adult": false,
                    "overview": "After her mother's death, six-year-old Frida is sent to her uncle's family to live with them in the countryside. But Frida finds it hard to forget her mother and adapt to her new life.",
                    "release_date": "2017-06-30"
                }
            ]
        }
    """.trimIndent()
}