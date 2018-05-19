package xyz.arnau.muvicat.cache.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE
import android.arch.persistence.room.Index
import android.arch.persistence.room.PrimaryKey

@Entity(
    tableName = "cast_members", indices = [(Index(value = ["movieId"], name = "castMemberMovieId"))],
    primaryKeys = ["tmdbId", "movieId"],
    foreignKeys = [ForeignKey(
        entity = MovieEntity::class,
        parentColumns = ["id"],
        childColumns = ["movieId"],
        onDelete = CASCADE
    )]
)
data class CastMemberEntity(
    val tmdbId: Int,
    var movieId: Long,
    val order: Int?,
    val name: String,
    val character: String?,
    val profile_path: String?
)