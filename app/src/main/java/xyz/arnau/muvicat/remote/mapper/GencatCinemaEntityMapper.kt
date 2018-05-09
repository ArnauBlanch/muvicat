package xyz.arnau.muvicat.remote.mapper

import xyz.arnau.muvicat.cache.model.CinemaEntity
import xyz.arnau.muvicat.remote.model.GencatCinema

class GencatCinemaEntityMapper : EntityMapper<GencatCinema, CinemaEntity> {
    override fun mapFromRemote(type: GencatCinema): CinemaEntity? {
        if (type.id == null) return null
        checkNullValues(type)
        if (type.name == null || type.address == null) return null
        val postalCode = parsePostalCode(type)
        return CinemaEntity(
            type.id!!.toLong(),
            type.name!!,
            type.address!!,
            type.town,
            type.region,
            type.province,
            postalCode
        )
    }

    private fun parsePostalCode(type: GencatCinema): Int? {
        var postalCode = ""
        for (char in type.address!!.asIterable()) {
            if (Character.isDigit(char)) {
                postalCode += char
                if (postalCode.length == 5) return postalCode.toInt()
            } else {
                postalCode = ""
            }
        }
        return null
    }

    private fun checkNullValues(t: GencatCinema) {
        t.name = if (t.name != "--" && t.name != "") t.name else null
        t.address = if (t.address != "--" && t.address != "") t.address else null
        t.town = if (t.town != "--" && t.town != "") t.town else null
        t.region = if (t.region != "--" && t.region != "") t.region else null
        t.province = if (t.province != "--" && t.province != "") t.province else null
    }
}