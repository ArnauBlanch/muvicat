package xyz.arnau.muvicat.remote.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "SESSIONS", strict = false)
data class GencatShowing(
    @field:Element(name = "IDFILM", required = false)
    var movieId: Int? = null,

    @field:Element(name = "CINEID", required = false)
    var cinemaId: Int? = null,

    @field:Element(name = "ses_data", required = false)
    var date: String? = null,

    @field:Element(name = "ver", required = false)
    var version: String? = null,

    @field:Element(name = "CICLEID", required = false)
    var seasonId: Int? = null
)