package xyz.arnau.muvicat.ui

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Singleton

@Singleton
class UiPreferencesHelper constructor(context: Context) {

    companion object {
        const val PREF_BUFFER_PACKAGE_NAME = "xyz.arnau.muvicat.preferences"

        const val PREF_KET_NEARBY_SHOWINGS_DISTANCE = "nearby_showings_distance"
    }

    private val bufferPref: SharedPreferences

    init {
        bufferPref = context.getSharedPreferences(PREF_BUFFER_PACKAGE_NAME, Context.MODE_PRIVATE)
    }

    var nearbyShowingsDistance: Int
        get() = bufferPref.getInt(PREF_KET_NEARBY_SHOWINGS_DISTANCE, 35)
        set(distance) {
            bufferPref.edit().putInt(PREF_KET_NEARBY_SHOWINGS_DISTANCE, distance).apply()
        }
}
