package xyz.arnau.muvicat.remote.model.tmdb

data class TMDBStatusResponse(
    var status_code: Int,
    var status_message: String? = null
)