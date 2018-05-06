package xyz.arnau.muvicat.data.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Singleton

@Singleton
open class PreferencesHelper constructor(context: Context) {

    companion object {
        const val PREF_BUFFER_PACKAGE_NAME = "xyz.arnau.muvicat.preferences"

        const val PREF_KEY_LAST_MOVIE_UPDATE = "last_movie_update"
        const val PREF_KEY_MOVIES_ETAG = "movies_etag"

        const val PREF_KEY_LAST_CINEMA_UPDATE = "last_cinema_update"
        const val PREF_KEY_CINEMAS_ETAG = "cinemas_etag"
    }

    private val bufferPref: SharedPreferences

    init {
        bufferPref = context.getSharedPreferences(PREF_BUFFER_PACKAGE_NAME, Context.MODE_PRIVATE)
    }

    var movieslastUpdateTime: Long
        get() = bufferPref.getLong(PREF_KEY_LAST_MOVIE_UPDATE, 0)
        private set(lastCache) = bufferPref.edit().putLong(
            PREF_KEY_LAST_MOVIE_UPDATE, lastCache
        ).apply()

    var moviesETag: String?
        get() = bufferPref.getString(PREF_KEY_MOVIES_ETAG, null)
        set(moviesETag) {
            bufferPref.edit().putString(PREF_KEY_MOVIES_ETAG, moviesETag).apply()
        }

    fun moviesUpdated() {
        movieslastUpdateTime = System.currentTimeMillis()
    }

    var cinemaslastUpdateTime: Long
        get() = bufferPref.getLong(PREF_KEY_LAST_CINEMA_UPDATE, 0)
        private set(lastCache) = bufferPref.edit().putLong(
            PREF_KEY_LAST_CINEMA_UPDATE, lastCache
        ).apply()

    var cinemasETag: String?
        get() = bufferPref.getString(PREF_KEY_CINEMAS_ETAG, null)
        set(cinemasETag) {
            bufferPref.edit().putString(PREF_KEY_CINEMAS_ETAG, cinemasETag).apply()
        }

    fun cinemasUpdated() {
        cinemaslastUpdateTime = System.currentTimeMillis()
    }
}
