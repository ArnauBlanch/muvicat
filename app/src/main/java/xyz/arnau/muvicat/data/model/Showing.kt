package xyz.arnau.muvicat.data.model

import android.arch.persistence.room.Ignore
import java.util.*

data class Showing(
    var id: Long?,
    var date: Date,
    var version: String?,
    var seasonId: Long?,

    var movieId: Long,
    var movieTitle: String?,
    var moviePosterUrl: String?,

    var cinemaName: String,
    var cinemaTown: String?,
    var cinemaRegion: String?,
    var cinemaProvince: String?,
    var cinemaLatitude: Double?,
    var cinemaLongitude: Double?
) {
    @Ignore
    var cinemaDistance: Int? = null
}