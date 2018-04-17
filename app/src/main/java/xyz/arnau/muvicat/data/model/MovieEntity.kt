package xyz.arnau.muvicat.data.model

import java.util.*

data class MovieEntity(
    var id: Long,
    var title: String?,
    var originalTitle: String?,
    var year: Int?,
    var direction: String?,
    var cast: String?,
    var plot: String?,
    var releaseDate: Date?,
    var posterUrl: String?,
    var priority: Int?,
    var originalLanguage: String?,
    var ageRating: String?,
    var trailerUrl: String?
)