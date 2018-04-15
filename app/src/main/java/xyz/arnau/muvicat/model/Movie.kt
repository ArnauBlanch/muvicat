package xyz.arnau.muvicat.model

import android.arch.persistence.room.Entity
import android.arch.persistence.room.Ignore
import android.arch.persistence.room.PrimaryKey
import org.simpleframework.xml.Element
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Commit
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

@Entity
@Root(name = "FILM", strict = false)
data class Movie(
        @field:Element(name = "IDFILM") @PrimaryKey
        var id: Int? = null,

        @field:Element(name = "TITOL")
        var title: String? = null,

        @field:Element(name = "CARTELL")
        var posterUrl: String? = null,

        @field:Element(name = "ESTRENA") @Ignore
        var releaseDateString: String? = null,

        var releaseDate: Date? = null,

        @field:Element(name = "ANY") @Ignore
        var yearString: String? = null,

        var year: Int? = null,

        @field:Element(name = "DIRECCIO")
        var direction: String? = null,

        @field:Element(name = "INTERPRETS")
        var cast: String? = null,

        @field:Element(name = "PRIORITAT")
        var priority: Int? = null,

        @field:Element(name = "ORIGINAL")
        var originalTitle: String? = null,

        @field:Element(name = "SINOPSI")
        var plot: String? = null,

        @field:Element(name = "IDIOMA_x0020_ORIGINAL")
        var originalLanguage: String? = null,

        @field:Element(name = "QUALIFICACIO")
        var ageRating: String? = null,

        @field:Element(name = "TRAILER")
        var trailerId: String? = null
) {
    @Commit
    private fun extraParsing() {
        parseReleaseDate()
        parseYear()
        setNullIfUnknownValues()
    }

    private fun parseReleaseDate() {
        val dateFormat = SimpleDateFormat("dd/MM/yyyy")
        releaseDate = try {
            dateFormat.parse(releaseDateString)
        } catch (e: ParseException) {
            null
        }
    }

    private fun parseYear() {
        if (yearString != "--" && yearString != null) {
            year = yearString!!.split('-')[0].toInt()
        }
    }

    private fun setNullIfUnknownValues() {
        title = if (title != "--") title else null
        posterUrl = if (posterUrl != "--") posterUrl else null
        direction = if (direction != "--") direction else null
        cast = if (cast != "--") cast else null
        originalTitle = if (originalTitle != "--") originalTitle else null
        plot = if (plot != "--") plot else null
        originalLanguage = if (originalLanguage != "--") originalLanguage else null
        ageRating = if (ageRating != "--") ageRating else null
        trailerId = if (trailerId != "--") trailerId else null
    }
}