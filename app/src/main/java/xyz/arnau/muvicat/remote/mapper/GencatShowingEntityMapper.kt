package xyz.arnau.muvicat.remote.mapper

import android.annotation.SuppressLint
import xyz.arnau.muvicat.cache.model.ShowingEntity
import xyz.arnau.muvicat.remote.model.gencat.GencatShowing
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class GencatShowingEntityMapper : EntityMapper<GencatShowing, ShowingEntity> {
    override fun mapFromRemote(type: GencatShowing): ShowingEntity? {
        if (type.movieId == null || type.cinemaId == null || type.date == null) return null
        checkNullValues(type)
        val date = parseDate(type) ?: return null
        return ShowingEntity(
            null,
            type.movieId!!.toLong(),
            type.cinemaId!!.toLong(),
            date,
            type.version,
            type.seasonId?.toLong()
        )
    }


    @SuppressLint("SimpleDateFormat")
    private fun parseDate(type: GencatShowing): Date? {
        if (type.date != null) {
            val dateFormat = SimpleDateFormat("dd/MM/yyyy")
            return try {
                dateFormat.parse(type.date)
            } catch (e: ParseException) {
                null
            }
        }
        return null
    }

    private fun checkNullValues(t: GencatShowing) {
        t.version = if (t.version != "--" && t.version != "") t.version else null
        t.seasonId = if (t.seasonId != 1 && t.seasonId != 0) t.seasonId else null
    }
}