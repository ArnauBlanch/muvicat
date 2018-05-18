package xyz.arnau.muvicat.repository.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity
data class MovieShowing(
    @PrimaryKey
    var id: Long?,
    var date: Date,
    var version: String?,

    var cinemaId: Long,
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