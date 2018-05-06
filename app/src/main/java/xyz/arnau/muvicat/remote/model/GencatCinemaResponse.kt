package xyz.arnau.muvicat.remote.model

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "dataroot", strict = false)
data class GencatCinemaResponse(
    @field:ElementList(name = "CINEMES", inline = true)
    var cinemaList: List<GencatCinema>? = null
)
