package xyz.arnau.muvicat.remote.model

import org.simpleframework.xml.Element
import org.simpleframework.xml.Root

//@Element(name = "FILM")
@Root(name = "FILM", strict = false)
data class GencatMovieModel(
    @field:Element(name = "IDFILM")
    var id: Int? = null, // TODO: Test rare cases

    @field:Element(name = "TITOL")
    var title: String? = null,

    @field:Element(name = "ORIGINAL")
    var originalTitle: String? = null,

    @field:Element(name = "ANY")
    var year: String? = null,

    @field:Element(name = "DIRECCIO")
    var direction: String? = null,

    @field:Element(name = "INTERPRETS")
    var cast: String? = null,

    @field:Element(name = "SINOPSI")
    var plot: String? = null,

    @field:Element(name = "ESTRENA")
    var releaseDate: String? = null,

    @field:Element(name = "CARTELL")
    var posterUrl: String? = null,

    @field:Element(name = "PRIORITAT")
    var priority: Int? = null,

    @field:Element(name = "IDIOMA_x0020_ORIGINAL")
    var originalLanguage: String? = null,

    @field:Element(name = "QUALIFICACIO")
    var ageRating: String? = null,

    @field:Element(name = "TRAILER")
    var trailerUrl: String? = null
)