package xyz.arnau.muvicat.repository.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Singleton

@Singleton
class RepoPreferencesHelper constructor(context: Context) {

    companion object {
        const val PREF_BUFFER_PACKAGE_NAME = "xyz.arnau.muvicat.preferences"

        const val PREF_KEY_LAST_MOVIE_UPDATE = "last_movie_update"
        const val PREF_KEY_LAST_CINEMA_UPDATE = "last_cinema_update"
        const val PREF_KEY_LAST_SHOWING_UPDATE = "last_showing_update"
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

    fun moviesUpdated() {
        movieslastUpdateTime = System.currentTimeMillis()
    }

    var cinemaslastUpdateTime: Long
        get() = bufferPref.getLong(PREF_KEY_LAST_CINEMA_UPDATE, 0)
        private set(lastCache) = bufferPref.edit().putLong(
            PREF_KEY_LAST_CINEMA_UPDATE, lastCache
        ).apply()

    fun cinemasUpdated() {
        cinemaslastUpdateTime = System.currentTimeMillis()
    }

    var showingslastUpdateTime: Long
        get() = bufferPref.getLong(PREF_KEY_LAST_SHOWING_UPDATE, 0)
        private set(lastCache) = bufferPref.edit().putLong(
            PREF_KEY_LAST_SHOWING_UPDATE, lastCache
        ).apply()

    fun showingsUpdated() {
        showingslastUpdateTime = System.currentTimeMillis()
    }
}
