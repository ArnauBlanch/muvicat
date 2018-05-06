package xyz.arnau.muvicat.remote.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "CINEMES", strict = false)
data class GencatCinema(
    @field:Element(name = "CINEID", required = false)
    var id: Int? = null,

    @field:Element(name = "CINENOM", required = false)
    var name: String? = null,

    @field:Element(name = "CINEADRECA", required = false)
    var address: String? = null,

    @field:Element(name = "LOCALITAT", required = false)
    var town: String? = null,

    @field:Element(name = "COMARCA", required = false)
    var region: String? = null,

    @field:Element(name = "PROVINCIA", required = false)
    var province: String? = null
)