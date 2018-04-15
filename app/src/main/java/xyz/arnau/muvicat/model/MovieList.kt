package xyz.arnau.muvicat.model

import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root

@Root(name = "dataroot", strict = false)
data class MovieList(
        @field:ElementList(name = "FILM", inline = true)
        var moviesList: List<Movie>? = null
)
