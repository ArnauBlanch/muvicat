package xyz.arnau.muvicat.cache.model

data class MovieExtraInfo(
    val tmdbId: Int,
    val runtime: Int?,
    val genres: List<String>,
    val backdropUrl: String?,
    val voteAverage: Double?,
    val voteCount: Int?,
    val cast: List<CastMemberEntity>
)