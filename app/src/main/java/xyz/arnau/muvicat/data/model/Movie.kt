package xyz.arnau.muvicat.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(tableName = "movies")
data class Movie(
        @PrimaryKey
        var id: Long?,
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