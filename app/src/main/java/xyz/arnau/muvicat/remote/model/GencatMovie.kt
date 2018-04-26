package xyz.arnau.muvicat.remote.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

@Root(name = "FILM", strict = false)
data class GencatMovie(
    @field:Element(name = "IDFILM", required = false)
    var id: Int? = null, // TODO: Test rare cases

    @field:Element(name = "TITOL", required = false)
    var title: String? = null,

    @field:Element(name = "ORIGINAL", required = false)
    var originalTitle: String? = null,

    @field:Element(name = "ANY", required = false)
    var year: String? = null,

    @field:Element(name = "DIRECCIO", required = false)
    var direction: String? = null,

    @field:Element(name = "INTERPRETS", required = false)
    var cast: String? = null,

    @field:Element(name = "SINOPSI", required = false)
    var plot: String? = null,

    @field:Element(name = "ESTRENA", required = false)
    var releaseDate: String? = null,

    @field:Element(name = "CARTELL", required = false)
    var posterUrl: String? = null,

    @field:Element(name = "PRIORITAT", required = false)
    var priority: Int? = null,

    @field:Element(name = "QUALIFICACIO", required = false)
    var ageRating: String? = null,

    @field:Element(name = "TRAILER", required = false)
    var trailerUrl: String? = null,

    @field:Element(name = "IDIOMA_x0020_ORIGINAL", required = false)
    var originalLanguage: String? = null
)