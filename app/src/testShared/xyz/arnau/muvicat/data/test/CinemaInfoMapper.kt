package xyz.arnau.muvicat.data.test

import xyz.arnau.muvicat.data.model.Cinema
import xyz.arnau.muvicat.data.model.CinemaInfo

object CinemaInfoMapper {
    fun mapFromCinema(cinema: Cinema) =
        CinemaInfo(
            cinema.id,
            cinema.name,
            cinema.address,
            cinema.town,
            cinema.region,
            cinema.province,
            null,
            null
        )

    fun mapFromCinemaList(cinemaList: List<Cinema>) =
        cinemaList.map { mapFromCinema(it) }
}