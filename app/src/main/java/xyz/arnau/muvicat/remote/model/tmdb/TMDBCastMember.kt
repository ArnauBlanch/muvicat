package xyz.arnau.muvicat.remote.model.tmdb

data class TMDBCastMember(
    val id: Int,
    val order: Int?,
    val name: String,
    val character: String?,
    val profile_path: String?
)