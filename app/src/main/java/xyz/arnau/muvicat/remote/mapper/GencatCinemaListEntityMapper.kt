package xyz.arnau.muvicat.remote.mapper

import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.remote.model.GencatCinemaResponse

class GencatCinemaListEntityMapper constructor(private val itemMapper: GencatCinemaEntityMapper) :
    EntityMapper<GencatCinemaResponse?, List<Cinema>?> {

    override fun mapFromRemote(type: GencatCinemaResponse?): List<Cinema>? =
        type?.cinemaList?.mapNotNull { itemMapper.mapFromRemote(it) }
}