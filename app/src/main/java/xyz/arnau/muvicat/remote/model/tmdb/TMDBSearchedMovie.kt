package xyz.arnau.muvicat.remote.model.tmdb

data class TMDBSearchedMovie(
    var id: Int,
    var original_title: String,
    var genre_ids: List<Int>,
    var backdrop_path: String?
)