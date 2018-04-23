package xyz.arnau.muvicat.remote.mapper

import android.annotation.SuppressLint
import xyz.arnau.muvicat.data.model.Movie
import xyz.arnau.muvicat.remote.model.GencatMovie
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class GencatMovieEntityMapper : EntityMapper<GencatMovie, Movie> {
    override fun mapFromRemote(type: GencatMovie): Movie? {
        if (type.id == null) return null
        checkNullValues(type)
        val releaseDate = parseReleaseDate(type)
        val year = parseYear(type)
        return Movie(
                type.id!!.toLong(),
                type.title,
                type.originalTitle,
                year,
                type.direction,
                type.cast,
                type.plot,
                releaseDate,
                type.posterUrl,
                type.priority,
                type.originalLanguage,
                type.ageRating,
                type.trailerUrl
        )
    }


    private fun parseYear(type: GencatMovie): Int? {
        return try {
            type.year?.split('-')?.get(0)?.toInt() // $COVERAGE-IGNORE$
        } catch (e: Exception) {
            null
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun parseReleaseDate(type: GencatMovie): Date? {
        if (type.releaseDate != null) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            return try {
                dateFormat.parse(type.releaseDate)
            } catch (e: ParseException) {
                null
            }
        }
        return null
    }

    private fun checkNullValues(t: GencatMovie) {
        t.title = if (t.title != "--" && t.title != "") t.title else null
        t.posterUrl =
                if (t.posterUrl != "--" && t.posterUrl != "") t.posterUrl else null
        t.direction =
                if (t.direction != "--" && t.direction != "") t.direction else null
        t.year = if (t.year != "--" && t.year != "") t.year else null
        t.releaseDate = if (t.releaseDate != "--" && t.releaseDate != "") t.releaseDate else null
        t.cast = if (t.cast != "--" && t.cast != "") t.cast else null
        t.originalTitle =
                if (t.originalTitle != "--" && t.originalTitle != "") t.originalTitle else null
        t.plot = if (t.plot != "--" && t.plot != "") t.plot else null
        t.originalLanguage =
                if (t.originalLanguage != "--" && t.originalLanguage != "") t.originalLanguage else null
        t.ageRating =
                if (t.ageRating != "--" && t.ageRating != "") t.ageRating else null
        t.trailerUrl =
                if (t.trailerUrl != "--" && t.trailerUrl != "") t.trailerUrl else null
    }


}