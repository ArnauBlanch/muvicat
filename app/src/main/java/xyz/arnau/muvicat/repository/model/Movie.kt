package xyz.arnau.muvicat.repository.model

import android.arch.persistence.room.ColumnInfo
import android.arch.persistence.room.Entity
import java.util.*

@Entity
data class Movie(
    var id: Long,
    var title: String?,
    var originalTitle: String?,
    var year: Int?,
    var direction: String?,
    @ColumnInfo(name = "castList")
    var cast: String?,
    var plot: String?,
    var releaseDate: Date?,
    var posterUrl: String?,
    var priority: Int?,
    var originalLanguage: String?,
    var ageRating: String?,
    var trailerUrl: String?,
    var tmdbId: Int?,
    var runtime: Int?,
    var genres: List<String>?,
    var backdropUrl: String?,
    var voteAverage: Double?,
    var voteCount: Int?
)