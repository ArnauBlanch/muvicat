package xyz.arnau.muvicat.remote.model

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "dataroot", strict = false)
data class GencatMovieResponse(
        @field:ElementList(name = "FILM", inline = true)
        var moviesList: List<GencatMovie>? = null
)
