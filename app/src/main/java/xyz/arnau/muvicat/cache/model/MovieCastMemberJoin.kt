package xyz.arnau.muvicat.cache.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.ForeignKey
import android.arch.persistence.room.ForeignKey.CASCADE

@Entity(
    tableName = "movie_cast_member_join", primaryKeys = ["movieId", "castMemberId"],
    foreignKeys = [
        ForeignKey(
            entity = MovieEntity::class,
            parentColumns = ["id"],
            childColumns = ["movieId"],
            onDelete = CASCADE
        ),
        ForeignKey(
            entity = CastMemberEntity::class,
            parentColumns = ["id"],
            childColumns = ["castMemberId"]
        )
    ]
)
data class MovieCastMemberJoin(
    val movieId: Long,
    val castMemberId: Int
)