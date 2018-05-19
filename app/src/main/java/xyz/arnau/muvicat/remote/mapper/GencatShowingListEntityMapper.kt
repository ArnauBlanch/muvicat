package xyz.arnau.muvicat.remote.mapper

import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.remote.model.gencat.GencatShowingResponse

class GencatShowingListEntityMapper constructor(private val itemMapper: GencatShowingEntityMapper) :
    EntityMapper<GencatShowingResponse?, List<ShowingEntity>?> {

    override fun mapFromRemote(type: GencatShowingResponse?): List<ShowingEntity>? =
        type?.showingList?.mapNotNull { itemMapper.mapFromRemote(it) }
}