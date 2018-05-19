package xyz.arnau.muvicat.repository.test

import xyz.arnau.muvicat.cache.model.CastMemberEntity
import xyz.arnau.muvicat.repository.model.CastMember

object CastMemberMapper {
    private fun mapFromCastMemberEntity(castMember: CastMemberEntity) =
        CastMember(
            castMember.tmdbId,
            castMember.movieId,
            castMember.order,
            castMember.name,
            castMember.character,
            castMember.profile_path
        )

    fun mapFromCastMemberEntityList(castList: List<CastMemberEntity>) =
        castList.map { mapFromCastMemberEntity(it) }
}