package xyz.arnau.muvicat.remote.mapper

import xyz.arnau.muvicat.data.model.Showing
import xyz.arnau.muvicat.remote.model.GencatShowingResponse

class GencatShowingListEntityMapper constructor(private val itemMapper: GencatShowingEntityMapper) :
    EntityMapper<GencatShowingResponse?, List<Showing>?> {

    override fun mapFromRemote(type: GencatShowingResponse?): List<Showing>? =
        type?.showingList?.mapNotNull { itemMapper.mapFromRemote(it) }
}