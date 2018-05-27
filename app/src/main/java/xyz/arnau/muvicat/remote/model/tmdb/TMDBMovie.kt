package xyz.arnau.muvicat.remote.model.tmdb

data class TMDBMovie(
    var id: Int,
    var runtime: Int?,
    var vote_average: Double,
    var vote_count: Int,
    var credits: TMDBCredits
)