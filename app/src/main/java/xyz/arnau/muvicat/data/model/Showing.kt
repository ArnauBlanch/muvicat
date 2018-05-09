package xyz.arnau.muvicat.data.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "showings",
    foreignKeys = [
        (ForeignKey(
            entity = Movie::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        )),
        (ForeignKey(
            entity = Cinema::class,
            parentColumns = ["id"],
            childColumns = ["cinemaId"],
            onUpdate = CASCADE,
            onDelete = CASCADE
        ))
    ],
    indices = [
        (Index(value = ["movieId"], name = "showingMovieId")),
        (Index(value = ["cinemaId"], name = "showingCinemaId")),
        (Index(value = ["date"], name = "showingDateId"))
    ]
)
data class Showing(
    @PrimaryKey(autoGenerate = true)
    var id: Long?,
    var movieId: Long,
    var cinemaId: Long,
    var date: Date,
    var version: String?,
    var seasonId: Long?
)