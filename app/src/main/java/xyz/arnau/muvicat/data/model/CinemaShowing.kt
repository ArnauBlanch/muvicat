package xyz.arnau.muvicat.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
data class CinemaShowing(
    @PrimaryKey
    var id: Long?,
    var date: Date,
    var version: String?,

    var movieId: Long,
    var movieTitle: String?,
    var moviePosterUrl: String?
)