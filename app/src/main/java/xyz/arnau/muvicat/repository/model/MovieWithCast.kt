package xyz.arnau.muvicat.repository.model

import android.arch.persistence.room.Embedded
import android.arch.persistence.room.Relation
import xyz.arnau.muvicat.cache.model.CastMemberEntity

data class MovieWithCast(
    @Embedded
    var movie: Movie
) {

    @Relation(
        parentColumn = "id",
        entityColumn = "movieId",
        entity = CastMemberEntity::class,
        projection = ["tmdbId", "order", "name", "character", "profile_path"]
    )
    var castMembers: List<CastMember> = listOf()
}