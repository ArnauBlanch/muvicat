package xyz.arnau.muvicat.remote.utils

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Singleton

@Singleton
class RemotePreferencesHelper constructor(context: Context) {

    companion object {
        const val PREF_BUFFER_PACKAGE_NAME = "xyz.arnau.muvicat.preferences"

        const val PREF_KEY_MOVIES_ETAG = "movies_etag"
        const val PREF_KEY_CINEMAS_ETAG = "cinemas_etag"
        const val PREF_KEY_SHOWINGS_ETAG = "showings_etag"
    }

    private val bufferPref: SharedPreferences

    init {
        bufferPref = context.getSharedPreferences(PREF_BUFFER_PACKAGE_NAME, Context.MODE_PRIVATE)
    }

    var moviesETag: String?
        get() = bufferPref.getString(PREF_KEY_MOVIES_ETAG, null)
        set(moviesETag) {
            bufferPref.edit().putString(PREF_KEY_MOVIES_ETAG, moviesETag).apply()
        }

    var cinemasETag: String?
        get() = bufferPref.getString(PREF_KEY_CINEMAS_ETAG, null)
        set(cinemasETag) {
            bufferPref.edit().putString(PREF_KEY_CINEMAS_ETAG, cinemasETag).apply()
        }

    var showingsETag: String?
        get() = bufferPref.getString(PREF_KEY_SHOWINGS_ETAG, null)
        set(showingsETag) {
            bufferPref.edit().putString(PREF_KEY_SHOWINGS_ETAG, showingsETag).apply()
        }
}
