package xyz.arnau.muvicat.remote.mapper

import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.remote.model.gencat.GencatCinemaResponse

class GencatCinemaListEntityMapper constructor(private val itemMapper: GencatCinemaEntityMapper) :
    EntityMapper<GencatCinemaResponse?, List<CinemaEntity>?> {

    override fun mapFromRemote(type: GencatCinemaResponse?): List<CinemaEntity>? =
        type?.cinemaList?.mapNotNull { itemMapper.mapFromRemote(it) }
}