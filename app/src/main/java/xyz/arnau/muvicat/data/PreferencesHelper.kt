package xyz.arnau.muvicat.data

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
open class PreferencesHelper @Inject constructor(context: Context) {

    companion object {
        const val PREF_BUFFER_PACKAGE_NAME = "xyz.arnau.muvicat.preferences"

        const val PREF_KEY_LAST_MOVIE_UPDATE = "last_movie_update"
        const val PREF_KEY_MOVIES_ETAG = "movies_etag"
    }

    private val bufferPref: SharedPreferences

    init {
        bufferPref = context.getSharedPreferences(PREF_BUFFER_PACKAGE_NAME, Context.MODE_PRIVATE)
    }

    var movieslastUpdateTime: Long
        get() = bufferPref.getLong(PREF_KEY_LAST_MOVIE_UPDATE, 0)
        private set(lastCache) = bufferPref.edit().putLong(PREF_KEY_LAST_MOVIE_UPDATE, lastCache).apply()

    var moviesETag: String?
        get() = bufferPref.getString(PREF_KEY_MOVIES_ETAG, null)
        set(moviesETag) {
            bufferPref.edit().putString(PREF_KEY_MOVIES_ETAG, moviesETag).apply()
        }

    fun moviesUpdated() {
        movieslastUpdateTime = System.currentTimeMillis()
    }
}
